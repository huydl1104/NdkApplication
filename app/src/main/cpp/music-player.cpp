//
// Created by qaz on 8/10/21.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include <stdlib.h>
#include <libavutil/ffversion.h>
// 在 c++ 中采用 c 的这种编译方式
extern "C" {
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include "libswresample/swresample.h"
#include "libavutil/avutil.h"
}
#define TAG "YDL"
using namespace std;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_ndk_player_MusicPlayer_ffmpegVersion(JNIEnv *env, jobject thiz) {
    unsigned int version = avcodec_version();
    int ver_major,ver_minor,ver_micro;
    ver_major = (version>>16)&0xff;
    ver_minor = (version>>8)&0xff;
    ver_micro = (version)&0xff;
    //Current ffmpeg version is: 3.2.12 ,avcodec version is: 3752037=57.64.101
    __android_log_print(ANDROID_LOG_ERROR,TAG,"Current ffmpeg version is: %s ,avcodec version is: %d=%d.%d.%d\n",FFMPEG_VERSION,version,ver_major,ver_minor,ver_micro);
    std::string hello = "Hello from C++ ";
    return env->NewStringUTF(hello.c_str());
}

void release_mediaInfo_memory(JNIEnv *env,AVFormatContext *avFormatContext,jstring _url,const char *url){
    //回收 avFormatContext
    if (avFormatContext){
        avformat_close_input(&avFormatContext);
    }
    //回收 url 、_url
    env->ReleaseStringUTFChars(_url,url);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ndk_player_MusicPlayer_mediaInfo(JNIEnv *env, jobject thiz, jstring _url) {

    //将 url 转为 char* 
    const char *url = env->GetStringUTFChars(_url, NULL);
    av_register_all();
    AVFormatContext *avFormatContext = NULL;
    //打开文件流
    int avformatOpenInput = avformat_open_input(&avFormatContext, url, NULL, NULL);
    __android_log_print(ANDROID_LOG_ERROR,TAG,"open input  %d",avformatOpenInput );
    if(avformatOpenInput != 0){
        const char *string1 = av_err2str(avformatOpenInput);
        __android_log_print(ANDROID_LOG_ERROR,TAG,"open input fail 22 %s",string1 );
        release_mediaInfo_memory(env,avFormatContext,_url,url);
        return env->NewStringUTF(string1);
    }
    //找到需要的流数据
    int streamInfo = avformat_find_stream_info(avFormatContext, NULL);
    __android_log_print(ANDROID_LOG_ERROR,TAG,"find stream  %d",streamInfo );
    if(streamInfo < 0){
        const char *string1 = av_err2str(streamInfo);
        __android_log_print(ANDROID_LOG_ERROR,TAG,"find stream fail %s",string1);
        release_mediaInfo_memory(env,avFormatContext,_url,url);
        return env->NewStringUTF(string1);
    }
    //获取采样率和通道
    int findBestStream = av_find_best_stream(avFormatContext, AVMediaType::AVMEDIA_TYPE_VIDEO, -1,-1, NULL, 0);
    if (findBestStream < 0){
        const char *string1 = av_err2str(findBestStream);
        __android_log_print(ANDROID_LOG_ERROR,TAG,"find_best_stream  fail %s",string1);
        release_mediaInfo_memory(env,avFormatContext,_url,url);
        return env->NewStringUTF(string1);
    }

    AVStream *audio_stream = avFormatContext->streams[findBestStream];
    __android_log_print(ANDROID_LOG_ERROR,TAG,"采样率：%d, 通道数: %d", audio_stream->codecpar->sample_rate, audio_stream->codecpar->channels);

    release_mediaInfo_memory(env,avFormatContext,_url,url);

}
