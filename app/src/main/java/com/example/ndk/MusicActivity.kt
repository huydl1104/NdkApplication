package com.example.ndk

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ndk.player.MusicPlayer


class MusicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val player = MediaPlayer()
        val musicPlayer = MusicPlayer()
    }

}