package com.example.opengl.renderers

import android.content.Context
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author yudongliang
 * create time 2021-12-16
 * describe : 曲棍 游戏
 * 1、概念：
 * 卷曲顺序：逆时针的顺序排列顶点
 * 顶点着色器：确定每个节点的最终位置，都会被执行一次，再把这些顶点组成 点、线、三角形
 * 片段着色器：为每个顶点生成最终的颜色，都会执行一次。（类似屏幕上的一个像素）
 * 2、执行流程
 * 读取顶点数据--->执行顶点数据--->组装图元--->光栅化图元--->执行片段着色器--->写入桢缓冲区--->显示屏幕上
 *
 *
 *
 *
 *
 * 为什么使用着色器？
 *      opengl提供有固定的API实现特定的效果，但是很难更改，所以加入了着色器到可编程API，可以实现自定义的效果
 *
 *
 *
 */
class HockeyRenderer(val mContext: Context): GLSurfaceView.Renderer {
    val POSITION_COMPONENT_COUNT = 2 //定义一个定点都有 两个 分量 x，y

    //采用浮点数结构定义顶点数据
    val tableVertices = floatArrayOf(
        0f,0f,
        0f,14f,
        9f,14f,
        9f,0f
    )
    //两个三角形的顶点数据
    val tableVerticesTriangles = floatArrayOf(
        //triangles 1
        0f,0f,
        9f,14f,
        0f,14f,
        //triangles 2
        0f,0f,
        9f,0f,
        9f,14f,
        //line
        0f,7f,
        9f,7f,
        //mallets
        4.5f,2f,
        4.5f,12f
    )
    //使用float类型 占四个字节
    val BYTES_PER_FLOAT = 4
    private var vertexData: FloatBuffer?= null

    /**
     * 顶点着色器
     * 1、每个单一的顶点被调用，顶点着色器就会被调用一次，接着会在a_Position属性里面接受当前顶点的位置，属性被定义为 vec4 类型
     * 2、关键字 attribute 把这些属性放进着色器里面。
     * 3、main作为着色器的入口，将前面定义过的位置 a_Position复制到gl_Position中，gl_Position代表着色器的最终位置
     */
    val simpleVertexShader:String =
        """
            attribute vec4 a_Position;
            void main(){
                gl_Position = a_Position
            }
        """

    /**
     * 片段着色器
     * 1、mediump:所有float默认精度（中等精度），lowp（低精度）、mediump（中等精度）、highp（高精度）
     *      高精度是牺牲性能为代价的。默认中等精度 也是基于 速度和质量的权衡
     * 2、uniform是四分量向量 分别对应 红、绿、蓝、透明度，它会让每个顶点都使用同一个值，除非手动改变
     * 3、main着色器的入口。将u_Color复制给gl_FragColor
     *
     *
     */
    val simpleFragmentShader:String =
        """
            precision mediump float;
            uniform vec4 u_Color;
            void main(){
                gl_FragColor = u_Color;
            } 
        """



    init {
        /**
         * ByteBuffer.allocateDirect：分配一块本地内存，不被垃圾回收期管理，需要设置 buffer的大小
         * put：将虚拟机内存中 复制到 本地内存。（进程结束本地内存会被释放）
         */
        vertexData = ByteBuffer.allocateDirect(tableVerticesTriangles.size * BYTES_PER_FLOAT)
             .order(ByteOrder.nativeOrder()).asFloatBuffer().put(tableVerticesTriangles)

    }

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