package com.example.ndk

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.example.ndk.player.MusicPlayer
import java.io.File


class MusicActivity : AppCompatActivity() {

    var mMusicFile = File(Environment.getExternalStorageDirectory(), "video.mp4")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val player = MediaPlayer()
        val musicPlayer = MusicPlayer()
        musicPlayer.play(mMusicFile.absolutePath)
    }

}