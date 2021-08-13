package com.example.ndk.activity

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceHolder
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.example.ndk.FFJniMediaPlayer
import com.example.ndk.FFJniMediaPlayer.VIDEO_RENDER_ANWINDOW
import com.example.ndk.R
import kotlinx.android.synthetic.main.activity_native_media_player.*

class NativeMediaPlayerActivity : AppCompatActivity(), SurfaceHolder.Callback, FFJniMediaPlayer.EventCallback {


    private var mIsTouch = false
    private var mMediaPlayer :FFJniMediaPlayer? = null

    private val mVideoPath = Environment.getExternalStorageDirectory().absolutePath + "/byteflow/one_piece.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_media_player)
        surface_view.holder.addCallback(this)

        seek_bar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                mIsTouch = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Log.e("TEST", "onStopTrackingTouch() called with: progress = [" + seekBar.progress + "]")
//                if (mMediaPlayer != null) {
//                    mMediaPlayer.seekToPosition(mSeekBar.getProgress())
//                    mIsTouch = false
//                }
            }
        })
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mMediaPlayer = FFJniMediaPlayer().apply {
            addEventCallback(this@NativeMediaPlayerActivity)
            init(mVideoPath, VIDEO_RENDER_ANWINDOW, holder.surface)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    override fun onPlayerEvent(msgType: Int, msgValue: Float) {

    }
}