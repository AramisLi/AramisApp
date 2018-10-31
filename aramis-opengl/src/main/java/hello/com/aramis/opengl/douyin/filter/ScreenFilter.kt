package hello.com.aramis.opengl.douyin.filter

import android.content.Context
import android.opengl.GLES20
import hello.com.aramis.opengl.R
import hello.com.aramis.opengl.douyin.utils.OpenUtils
import java.lang.IllegalStateException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 *Created by Aramis
 *Date:2018/10/31
 *Description:负责往屏幕上渲染
 */
class ScreenFilter(val context: Context) {
    var mProgram: Int = 0

    private var vPosition: Int

    private var vCoord: Int

    private var vMatrix: Int

    private var vTexture: Int

    private var mTextureBuffer: FloatBuffer? = null
    private var fragmentBuffer: FloatBuffer? = null

    init {
        val vertexSource = OpenUtils.readRawTextFile(context, R.raw.camera_vertex)
        val fragmentSource = OpenUtils.readRawTextFile(context, R.raw.camera_frag)
        //通过字符串（代码）创建着色器程序
        //使用opengl
        //1.1 创建顶点着色器
        val vShaderId = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        //1.2 绑定代码到着色器中去
        GLES20.glShaderSource(vShaderId, vertexSource)
        //1.3 编译着色器代码
        GLES20.glCompileShader(vShaderId)
        //主动获取编译成功或者失败
        val status = intArrayOf(0)
        GLES20.glGetShaderiv(vShaderId, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] != GLES20.GL_TRUE) {
            throw IllegalStateException("ScreenFilter 顶点着色器配置失败!")
        }
        //2.创建片元着色器
        val fShaderId = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fShaderId, fragmentSource)
        GLES20.glCompileShader(fShaderId)
        GLES20.glGetShaderiv(fShaderId, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] != GLES20.GL_TRUE) {
            throw IllegalStateException("ScreenFilter 片元着色器配置失败!")
        }
        //3.创建着色器程序(运行在GPU上)
        mProgram = GLES20.glCreateProgram()
        //把着色器塞到程序当中去
        GLES20.glAttachShader(mProgram, vShaderId)
        GLES20.glAttachShader(mProgram, fShaderId)
        //链接着色器
        GLES20.glLinkProgram(mProgram)
        //判断是否成功
        GLES20.glGetShaderiv(fShaderId, GLES20.GL_LINK_STATUS, status, 0)
        if (status[0] != GLES20.GL_TRUE) {
            throw IllegalStateException("ScreenFilter 着色器程序配置失败!")
        }

        //因为已经塞到着色器程序当中了，所以删除
        GLES20.glDeleteShader(vShaderId)
        GLES20.glDeleteShader(fShaderId)

        vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition")
        vCoord = GLES20.glGetAttribLocation(mProgram, "vCoord")
        vMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix")
        vTexture = GLES20.glGetUniformLocation(mProgram, "vTexture")

        //创建一个数据缓冲区(4个数据*坐标xy*float类型长度4字节)
        mTextureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        val v = floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f)
        mTextureBuffer?.put(v)

        fragmentBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        val t = floatArrayOf(0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f)
        fragmentBuffer?.put(t)
    }


    fun onDrawFrame(texture: Int, mtx: FloatArray) {
        //1. 设置窗口 画画的时候，画布是5*5

        GLES20.glViewport(0, 0, mWidth, mHeight)
        //2. 使用着色器程序
        GLES20.glUseProgram(mProgram)
        //3. 获取着色器程序中的变量索引，通过索引传值
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer)
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, fragmentBuffer)


    }

    private var mWidth: Int = 0

    private var mHeight: Int = 0

    fun onReady(width: Int, height: Int) {
        mWidth = width
        mHeight = height
    }
}