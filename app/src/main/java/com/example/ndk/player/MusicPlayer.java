package com.example.ndk.player;


/**
 * @author yudongliang
 * create time 2021-08-11
 * describe :
 */
public class MusicPlayer {

    static {
        System.loadLibrary("music-player");
    }

    /**
     * ffmpeg 版本号
     */
    public native String ffmpegVersion();

    /**
     * 得到音频 meta 信息
     */
    public native void mediaInfo(String url);


}
