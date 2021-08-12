package com.example.ndk.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ndk.R

class NativeMediaPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_media_player)
    }
}