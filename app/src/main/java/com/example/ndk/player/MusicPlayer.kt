package com.example.ndk.player

/**
 * @author yudongliang
 * create time 2021-08-10
 * describe : 音乐播放器
 */
class MusicPlayer {

    //开始播放音频
    external fun play(url:String): String

    companion object {

        init {
            System.loadLibrary("music-player")
        }
    }

}