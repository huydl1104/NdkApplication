package com.example.ndk;

import android.view.Surface;

/**
 * @author yudongliang
 * create time 2021-08-12
 * describe :
 */
public class FFJniMediaPlayer {
    static {
        System.loadLibrary("main-ffmpeg");
    }
    //gl render type
    public static final int VIDEO_GL_RENDER = 0;
    public static final int AUDIO_GL_RENDER = 1;
    public static final int VR_3D_GL_RENDER = 2;
    public static final int MSG_DECODER_INIT_ERROR      = 0;
    public static final int MSG_DECODER_READY           = 1;
    public static final int MSG_DECODER_DONE            = 2;
    public static final int MSG_REQUEST_RENDER          = 3;
    public static final int MSG_DECODING_TIME           = 4;
    public static final int MEDIA_PARAM_VIDEO_WIDTH     = 0x0001;
    public static final int MEDIA_PARAM_VIDEO_HEIGHT    = 0x0002;
    public static final int MEDIA_PARAM_VIDEO_DURATION  = 0x0003;
    public static final int VIDEO_RENDER_OPENGL         = 0;
    public static final int VIDEO_RENDER_ANWINDOW       = 1;
    public static final int VIDEO_RENDER_3D_VR          = 2;

    private long mNativePlayerHandle = 0;

    public static String getFFVersion(){
        return nativeFFVersion();
    }


    private EventCallback mEventCallback;
    public void addEventCallback(EventCallback callback) {
        mEventCallback = callback;
    }

    public void init(String url, int videoRenderType, Surface surface) {
        mNativePlayerHandle = native_Init(url, videoRenderType, surface);
    }

    // c 调用的方法 ，名字需要和 c 内部的名字保持一直
    private void playerEventCallback(int msgType, float msgValue) {
        if(mEventCallback != null)
            mEventCallback.onPlayerEvent(msgType, msgValue);

    }

    private static native String nativeFFVersion();
    private native long native_Init(String url, int renderType, Object surface);


    public interface EventCallback {
        void onPlayerEvent(int msgType, float msgValue);
    }
}


