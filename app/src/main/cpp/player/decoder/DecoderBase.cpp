//
// Created by ydl on 8/13/21.
//

#include "DecoderBase.h"
#include "LogUtil.h"

//开始执行解码，考虑多线程的问题
void DecoderBase::Start() {
    if(m_Thread == nullptr){
        StartDecodingThread();
    }else{
        std::unique_lock<std::mutex> lock(m_Mutex);
        m_DecoderState = STATE_DECODING;
        m_Cond.notify_all();
    }
}

void DecoderBase::StartDecodingThread() {
    m_Thread = new thread(DoAVDecoding, this);
}

void DecoderBase::DoAVDecoding(DecoderBase *decoder) {
    LOGCATE("DecoderBase::DoAVDecoding");
    do {
        if(decoder->InitFFDecoder() != 0){
            break;
        }
        decoder->OnDecoderReady();
        decoder->DecodingLoop();
    }while (false);
    decoder->UnInitDecoder();
    decoder->OnDecoderDone();
}

void DecoderBase::DecodingLoop() {
    LOGCATE("DecoderBase::DecodingLoop start, m_MediaType=%d", m_MediaType);
    {
        std::unique_lock<std::mutex> lock(m_Mutex);
        m_DecoderState = STATE_DECODING;
        lock.unlock();
    }

    for (;;) {
        while (m_DecoderState == STATE_PAUSE){
            std::unique_lock<std::mutex> lock(m_Mutex);
            LOGCATE("DecoderBase::DecodingLoop waiting, m_MediaType=%d", m_MediaType);
            m_Cond.wait_for(lock, std::chrono::milliseconds(10));
            m_StartTimeStamp = GetSysCurrentTime() - m_CurTimeStamp;
        }
        if(m_DecoderState == STATE_STOP) {
            break;
        }

        if(m_StartTimeStamp == -1)
            m_StartTimeStamp = GetSysCurrentTime();

        if(DecodeOnePacket() != 0) {
            //解码结束，暂停解码器
            std::unique_lock<std::mutex> lock(m_Mutex);
            m_DecoderState = STATE_PAUSE;
        }
    }

    LOGCATE("DecoderBase::DecodingLoop end");
}

int DecoderBase::DecodeOnePacket() {
    //解码流中的没一个 packet
    if(m_SeekPosition > 0){
        //seek to frame
        int64_t seek_target = static_cast<int64_t>(m_SeekPosition * 1000000);//微妙
        int64_t seek_min = INT64_MIN;
        int64_t seek_max = INT64_MAX;
        int seek_ret= avformat_seek_file(m_AVFormatContext, m_StreamIndex, seek_min, seek_target,
                                          seek_max, 0);
        if(seek_ret < 0){
            m_SeekSuccess = false;
            LOGCATE("BaseDecoder::DecodeOneFrame error while seeking m_MediaType=%d", m_MediaType);
        }else{
            if(m_StreamIndex != -1){
               avcodec_flush_buffers(m_AVCodecContext);
            }
            ClearCache();
            m_SeekSuccess = true;
            LOGCATE("BaseDecoder::DecodeOneFrame seekFrame pos=%f, m_MediaType=%d", m_SeekPosition, m_MediaType);
        }
    }
    //读出编码的数据包
    int result = av_read_frame(m_AVFormatContext,m_Packet);
    while (result == 0){
        if (m_Packet->stream_index == m_StreamIndex){
            //将原始数据作为输入传递给解码器
            if(avcodec_send_packet(m_AVCodecContext,m_Packet) == AVERROR_EOF){
                //解码结束
                result = -1;
                goto __EXIT;
            }
            //检测packet中包含多少frame
            int frameCount = 0;
            //返回解码器，解码后的数据
            while (avcodec_receive_frame(m_AVCodecContext,m_Frame) == 0){
                UpdateTimeStamp();//更新时间戳
                AVSync();
                LOGCATE("DecoderBase::DecodeOnePacket 000 m_MediaType=%d", m_MediaType);
                //渲染
                OnFrameAvailable(m_Frame);
                frameCount++;
            }
            LOGCATE("BaseDecoder::DecodeOneFrame frameCount=%d", frameCount);
            //判断一个 packet 是否解码完成
            if(frameCount > 0) {
                result = 0;
                goto __EXIT;
            }
        }
        av_packet_unref(m_Packet);
        result = av_read_frame(m_AVFormatContext, m_Packet);
    }
    __EXIT:
    av_packet_unref(m_Packet);
    return result;
}

void DecoderBase::UpdateTimeStamp(){
    LOGCATE("DecoderBase::UpdateTimeStamp");
    std::unique_lock<std::mutex> lock(m_Mutex);

    if (m_Frame->pkt_dts != AV_NOPTS_VALUE){//解码时间
        m_CurTimeStamp = m_Frame->pkt_dts;
    } else if (m_Frame->pts != AV_NOPTS_VALUE) {//显示时间
        m_CurTimeStamp = m_Frame->pts;
    } else {
        m_CurTimeStamp = 0;
    }
    m_CurTimeStamp = (int64_t)((m_CurTimeStamp * av_q2d(m_AVFormatContext->streams[m_StreamIndex]->time_base)) * 1000);

    if(m_SeekPosition > 0 && m_SeekSuccess)
    {
        m_StartTimeStamp = GetSysCurrentTime() - m_CurTimeStamp;
        m_SeekPosition = 0;
        m_SeekSuccess = false;
    }

}
//同步音视频的数据
long DecoderBase::AVSync(){
    LOGCATE("DecoderBase::AVSync");
    long int curSysTime = GetSysCurrentTime();
    //基于系统时钟计算从开始播放流逝的时间
    long elapsedTime = curSysTime - m_StartTimeStamp;

    if(m_MsgContext && m_MsgCallback && m_MediaType == AVMEDIA_TYPE_AUDIO)
        m_MsgCallback(m_MsgContext, MSG_DECODING_TIME, m_CurTimeStamp * 1.0f / 1000);

    long delay = 0;
    if (m_CurTimeStamp > elapsedTime){
        //计算需要休眠的时间
        auto sleepTime = static_cast<unsigned int >(m_CurTimeStamp - elapsedTime);
        //限制休眠时间不能过长
        sleepTime = sleepTime > DELAY_THRESHOLD ? DELAY_THRESHOLD :  sleepTime;
        av_usleep(sleepTime * 1000);
    }
    delay = elapsedTime - m_CurTimeStamp;
    return delay;
}

void DecoderBase::Pause() {
    std::unique_lock<std::mutex> lock(m_Mutex);
    m_DecoderState = STATE_PAUSE;
}

void DecoderBase::Stop() {
    LOGCATE("DecoderBase::Stop");
    std::unique_lock<std::mutex> lock(m_Mutex);
    m_DecoderState = STATE_STOP;
    m_Cond.notify_all();
}

void DecoderBase::SeekToPosition(float position) {
    LOGCATE("DecoderBase::SeekToPosition position=%d",position);
    std::unique_lock<std::mutex> lock(m_Mutex);
    m_SeekPosition = position;
    m_DecoderState = DecoderState::STATE_DECODING;
    m_Cond.notify_all();
}

float DecoderBase::GetCurrentPosition() {
    return m_SeekPosition;
}

int DecoderBase::Init(const char *url, AVMediaType mediaType) {
    strcpy(m_Url,url);
    m_MediaType = mediaType;
    return 0;
}

void DecoderBase::UnInit() {
    LOGCATE("DecoderBase::UnInit start， m_MediaType=%d", m_MediaType);
    //回收线程占用的内存
    if(m_Thread){
        Stop();
        m_Thread->join();
        delete m_Thread;
        //置为 null。否则会变为野指针
        m_Thread = nullptr;
    }
    LOGCATE("DecoderBase::UnInit end, m_MediaType=%d", m_MediaType);
}

//初始化解码器
int DecoderBase::InitFFDecoder() {
    int result = -1;
    LOGCATE("DecoderBase::InitFFDecoder start --- ");
    //为了执行后面的回掉函数。初始化失败可以回调出去
    do {
        //1、初始化上下文
        m_AVFormatContext = avformat_alloc_context();
        //2、打开文件
        if (avformat_open_input(&m_AVFormatContext,m_Url,NULL,NULL) != 0){
            LOGCATE("DecoderBase::InitFFDecoder  avformat_open_input fail ");
            break;
        }
        //3、获取流的信息
        if(avformat_find_stream_info(m_AVFormatContext,NULL) < 0){
            LOGCATE("DecoderBase::InitFFDecoder  avformat_find_stream_info fail ");
            break;
        }
        //4、获取音视频流的索引
        for (int i = 0; i < m_AVFormatContext->nb_streams; ++i) {
            if(m_AVFormatContext->streams[i]->codecpar->codec_type == m_MediaType){
                m_StreamIndex = i;
                break;
            }
        }
        //若是没有找到进行校验
        if(m_StreamIndex == -1){
            LOGCATE("DecoderBase::InitFFDecoder Fail to find stream index.");
            break;
        }
        AVCodecParameters *codeParameters = m_AVFormatContext->streams[m_StreamIndex]->codecpar;
        //5、获取编解码器的参数
        m_AVCodec= avcodec_find_decoder(codeParameters->codec_id);
        if(m_AVCodec == nullptr) {
            LOGCATE("DecoderBase::InitFFDecoder avcodec_find_decoder fail.");
            break;
        }
        //6、获取解码器的上下文
        m_AVCodecContext = avcodec_alloc_context3(m_AVCodec);
        if(avcodec_parameters_to_context(m_AVCodecContext, codeParameters) != 0) {
            LOGCATE("DecoderBase::InitFFDecoder avcodec_parameters_to_context fail.");
            break;
        }
        //用来传递数据 AVDictionary
        AVDictionary *pAVDictionary = nullptr;
        av_dict_set(&pAVDictionary, "buffer_size", "1024000", 0);
        av_dict_set(&pAVDictionary, "stimeout", "20000000", 0);
        av_dict_set(&pAVDictionary, "max_delay", "30000000", 0);
        av_dict_set(&pAVDictionary, "rtsp_transport", "tcp", 0);
        result = avcodec_open2(m_AVCodecContext,m_AVCodec,&pAVDictionary);
        if(result != 0){
            LOGCATE("DecoderBase::InitFFDecoder avcodec_open2 fail. result=%d", result);
            break;
        }

        m_Duration = m_AVFormatContext->duration / AV_TIME_BASE * 1000;//us to ms
        //创建 AVPacket 存放编码数据
        m_Packet = av_packet_alloc();
        //创建 AVFrame 存放解码后的数据
        m_Frame = av_frame_alloc();
    }while (false);

    //回掉函数
    if(result != 0 && m_MsgContext && m_MsgCallback)
        m_MsgCallback(m_MsgContext, MSG_DECODER_INIT_ERROR, 0);

    LOGCATE("DecoderBase::InitFFDecoder end --- ");
    return result;
}


void DecoderBase::UnInitDecoder() {
    LOGCATE("DecoderBase::UnInitDecoder");
    if(m_AVFormatContext != nullptr){
       avformat_free_context(m_AVFormatContext);
       m_AVFormatContext = nullptr;
    }
    if(m_Packet != nullptr) {
        av_packet_free(&m_Packet);
        m_Packet = nullptr;
    }

    if(m_AVCodecContext != nullptr) {
        avcodec_close(m_AVCodecContext);
        avcodec_free_context(&m_AVCodecContext);
        m_AVCodecContext = nullptr;
        m_AVCodec = nullptr;
    }

    if(m_AVFormatContext != nullptr) {
        avformat_close_input(&m_AVFormatContext);
        avformat_free_context(m_AVFormatContext);
        m_AVFormatContext = nullptr;
    }

}
