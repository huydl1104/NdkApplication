package com.example.opengl.renderers

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.opengl.ShaderHelper
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
 * opengl程序：将 顶点着色器和片段着色器连接起来变成单个对象
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


    //采用浮点数结构定义顶点数据
    val tableVertices = floatArrayOf(
        0f,0f,
        0f,14f,
        9f,14f,
        9f,0f
    )
    //两个三角形的顶点数据
/*    val tableVerticesTriangles = floatArrayOf(
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
    )*/
    val tableVerticesTriangles = floatArrayOf(
        0f,0f,1f,1f,1f,
        -0.5f,-0.8f,0.7f,0.7f,0.7f,
        0.5f,-0.8f,0.7f,0.7f,0.7f,

        0.5f,0.8f,0.7f,0.7f,0.7f,
        -0.5f,0.8f,0.7f,0.7f,0.7f,
        -0.5f,-0.8f,0.7f,0.7f,0.7f,

        -0.5f,0f,1f,0f,0f,
        0.5f,0f,1f,0f,0f,

        0f,-0.4f,0f,0f,1f,
        0f,0.4f,1f,0f,0f,

    )

    /**
     * 顶点着色器
     * 1、每个单一的顶点被调用，顶点着色器就会被调用一次，接着会在a_Position属性里面接受当前顶点的位置，属性被定义为 vec4 类型
     * 2、关键字 attribute 把这些属性放进着色器里面。
     * 3、main作为着色器的入口，将前面定义过的位置 a_Position复制到gl_Position中，gl_Position代表着色器的最终位置
     * 4、varying是特殊变量，将接受到的值混合后发给片段着色器
     */
    val simpleVertexShader:String =
        """
            uniform mat4 u_Matrix;
            attribute vec4 a_Position;
            attribute vec4 a_Color;
            varying vec4 v_Color;
            void main(){
                v_Color = a_Color;
                gl_Position = u_Matrix * a_Position;
                gl_PointSize = 20.0;
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
            varying vec4 v_Color;
            void main(){
                gl_FragColor = v_Color;
            } 
        """
    private val A_COLOR = "a_Color"
    private val A_POSITION = "a_Position"
    private val U_MATRIX = "u_Matrix"
    private var aColorLocation = 0
    private var aPositionLocation = 0
    private val POSITION_COMPONENT_COUNT = 2 //定义一个定点都有 两个 分量 x，y
    private val COLOR_COMPONENT_COUNT = 3 //color分量  r,g,b
    //使用float类型 占四个字节
    private val BYTES_PER_FLOAT = 4
    private var vertexData: FloatBuffer?= null
    private val STRIDE = (POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT)*BYTES_PER_FLOAT
    //用于存储矩阵的顶点数据位置
    private val projectionMatrix = FloatArray(16)
    //保存uniform位置
    private var uMatrixLocation = 0

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
        val vertexShader = ShaderHelper.compileVertexShader(simpleVertexShader)
        val fragShader = ShaderHelper.compileFragmentShader(simpleFragmentShader)
        //放入单个程序里里面
        val linkProgram = ShaderHelper.linkProgram(vertexShader, fragShader)
        if (!ShaderHelper.validateProgram(linkProgram)){
            Log.e("YUDL","validateProgram fail ->${glGetProgramInfoLog(linkProgram)}" )
            return
        }
        //使用创建好的程序
        glUseProgram(linkProgram)

        //获得uniform u_color的位置，以便在绘画的时候设置颜色
        aColorLocation = glGetAttribLocation(linkProgram, A_COLOR)
        //获取顶点属性的位置
        aPositionLocation = glGetAttribLocation(linkProgram,A_POSITION)
        //保证读取数据一定是从0开始读取
        vertexData?.position(0)
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,GL_FLOAT,false,STRIDE,vertexData)
        //告诉opengl 从vertexData 中找到a_position的数据
        glEnableVertexAttribArray(aPositionLocation)

        vertexData?.position(POSITION_COMPONENT_COUNT)
        glVertexAttribPointer(aColorLocation,COLOR_COMPONENT_COUNT, GL_FLOAT,false,STRIDE,vertexData)
        glEnableVertexAttribArray(aColorLocation)

        uMatrixLocation = glGetUniformLocation(linkProgram,U_MATRIX)

    }

    /**
     * 正交投影 解决横竖屏切换拉伸问题
     */
    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        val radio = if (width > height) width.toFloat()/height.toFloat() else height.toFloat()/width.toFloat()
        Log.e("YUDL","onSurfaceChanged  radio ->$radio , width ->$width ,height ->$height")
        if (width > height){//横屏
            Matrix.orthoM(projectionMatrix,0,-radio,radio,-1f,1f,-1f,1f)
        }else{
            Matrix.orthoM(projectionMatrix,0,-1f,1f,-radio,radio,-1f,1f)
        }
        gl.glViewport(0,0,width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        //清空到默认配置的颜色
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
        glUniformMatrix4fv(uMatrixLocation,1,false,projectionMatrix,0)
        //绘制三角形
        glDrawArrays(GL_TRIANGLE_FAN,0,6)

        //绘制直线
        glDrawArrays(GL_LINES,6,2)

        //绘制两个木追
        glDrawArrays(GL_POINTS,8,1)
        glDrawArrays(GL_POINTS,9,1)
    }
}