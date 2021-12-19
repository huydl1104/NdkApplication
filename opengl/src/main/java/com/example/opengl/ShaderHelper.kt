package com.example.opengl

import android.opengl.GLES20
import android.util.Log

object ShaderHelper {

    val TAG = "ShaderHelper"

    /**
     * 顶点着色器
     */
    fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_VERTEX_SHADER,shaderCode)
    }

    /**
     * 片段着色器
     */
    fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_FRAGMENT_SHADER,shaderCode)
    }

    private fun compileShader(glVertexShader: Int, shaderCode: String):Int {
        //1、创建 着色器
        val shaderObjectId = GLES20.glCreateShader(glVertexShader)
        if (shaderObjectId == 0){
            Log.e("YUDL","could not create new shader")
            return 0
        }
        //2、向着色器上设置资源
        GLES20.glShaderSource(shaderObjectId,shaderCode)
        //3、编译着色器
        GLES20.glCompileShader(shaderObjectId)
        if (!isCompileSuccess(shaderObjectId)){
            GLES20.glDeleteShader(shaderObjectId)
            Log.e("YUDL","compile shader fail  " )
            return 0
        }
        return shaderObjectId
    }

    /**
     * 检查是否compile success
     */
    fun isCompileSuccess(shaderId: Int):Boolean{
        val compileState = IntArray(1)
        GLES20.glGetShaderiv(shaderId,GLES20.GL_COMPILE_STATUS,compileState,0)
        return compileState[0] != 0
    }

    /**
     * 将 顶点着色器和片段着色器 链接在一起
     */
    fun linkProgram(vertexShaderId:Int,fragmentShaderId: Int):Int{
        val programObjectId = GLES20.glCreateProgram()
        if (programObjectId == 0){
            Log.e("YUDL","create linkProgram  fail " )
            return 0
        }
        //顶点着色器和片段着色器赋值到程序上
        val vertexShaderId = GLES20.glAttachShader(programObjectId, vertexShaderId)
        val fragmentShaderId = GLES20.glAttachShader(programObjectId, fragmentShaderId)
        if (!isLinkProgram(programObjectId)){
            GLES20.glDeleteProgram(programObjectId)
            Log.e("YUDL","link Program fail ->${GLES20.glGetProgramInfoLog(programObjectId)}" )
            return 0
        }
        return programObjectId
    }

    fun isLinkProgram(programObjectId: Int): Boolean {
        val state = IntArray(1)
        GLES20.glGetProgramiv(programObjectId,GLES20.GL_LINK_STATUS,state,0)
        return state[0] != 0
    }

}