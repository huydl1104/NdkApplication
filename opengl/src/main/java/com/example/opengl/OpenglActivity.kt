package com.example.opengl

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opengl.renderers.FirstRenderer
import com.example.opengl.renderers.HockeyRenderer
import kotlinx.android.synthetic.main.activity_opengl.*

class OpenglActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl)
        glSurfaceView.setEGLContextClientVersion(2)
        val renderer : GLSurfaceView.Renderer = HockeyRenderer(this)
        glSurfaceView.setRenderer(renderer)
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

}