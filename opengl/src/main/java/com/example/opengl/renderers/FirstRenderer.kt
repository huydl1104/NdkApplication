package com.example.opengl.renderers

import android.content.Context
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author yudongliang
 * create time 2021-12-16
 * describe : 第一个测试 renderer
 */
class FirstRenderer(val mContext: Context): GLSurfaceView.Renderer {


    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        gl.glClearColor(0.5f,0.5f,0.0f,0.0f)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0,0,width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
    }
}