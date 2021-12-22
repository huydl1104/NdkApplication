package com.example.opengl

import android.opengl.GLES20.*
import android.util.Log

object ShaderHelper {

    val TAG = "ShaderHelper"

    /**
     * 顶点着色器
     */
    fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GL_VERTEX_SHADER,shaderCode)
    }

    /**
     * 片段着色器
     */
    fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GL_FRAGMENT_SHADER,shaderCode)
    }

    private fun compileShader(glVertexShader: Int, shaderCode: String):Int {
        //1、创建 着色器
        val shaderObjectId = glCreateShader(glVertexShader)
        if (shaderObjectId == 0){
            Log.e("YUDL","could not create new shader")
            return 0
        }
        //2、向着色器上设置资源
        glShaderSource(shaderObjectId,shaderCode)
        //3、编译着色器
        glCompileShader(shaderObjectId)
        if (!isCompileSuccess(shaderObjectId)){
            glDeleteShader(shaderObjectId)
            Log.e("YUDL","compile shader fail  ->${glGetShaderInfoLog(shaderObjectId)}" )
            return 0
        }
        return shaderObjectId
    }

    /**
     * 检查是否compile success
     */
    private fun isCompileSuccess(shaderId: Int):Boolean{
        val compileState = IntArray(1)
        glGetShaderiv(shaderId,GL_COMPILE_STATUS,compileState,0)
        return compileState[0] != 0
    }

    /**
     * 将 顶点着色器和片段着色器 链接在一起
     */
    fun linkProgram(vertexShaderId:Int,fragmentShaderId: Int):Int{
        val programObjectId = glCreateProgram()
        if (programObjectId == 0){
            Log.e("YUDL","create linkProgram  fail " )
            return 0
        }
        //顶点着色器和片段着色器赋值到程序上
        glAttachShader(programObjectId, vertexShaderId)
        glAttachShader(programObjectId, fragmentShaderId)
        //链接程序
        glLinkProgram(programObjectId)
        //校验是否链接成功
        if (!isLinkProgram(programObjectId)){
            glDeleteProgram(programObjectId)
            Log.e("YUDL","link Program fail ->${glGetProgramInfoLog(programObjectId)}" )
            return 0
        }
        return programObjectId
    }

    private fun isLinkProgram(programObjectId: Int): Boolean {
        val state = IntArray(1)
        glGetProgramiv(programObjectId,GL_LINK_STATUS,state,0)
        Log.e("YUDL","compile program isLinkProgram  ->${state[0]}" )
        return state[0] != 0
    }

    /**
     * 检查opengl程序是否是有效的
     */
    fun validateProgram(programObjectId: Int): Boolean {
        glValidateProgram(programObjectId)
        val state = IntArray(1)
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS,state,0)
        return state[0] != 0
    }
}