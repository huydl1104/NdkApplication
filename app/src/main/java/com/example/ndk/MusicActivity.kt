package com.example.ndk

import android.Manifest
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.ndk.player.MusicPlayer
import java.io.File


class MusicActivity : AppCompatActivity() {

    private var mMusicFile = File(Environment.getExternalStorageDirectory(), "testvideo.mp4")
    private val musicPlayer = MusicPlayer()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val array =  arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
        this.requestPermissions(array,100)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val absolutePath = mMusicFile.absolutePath
        Log.e("YDL","absolutePath ->$absolutePath , exist ->${mMusicFile.exists()}")
        musicPlayer.mediaInfo(absolutePath)
    }
}