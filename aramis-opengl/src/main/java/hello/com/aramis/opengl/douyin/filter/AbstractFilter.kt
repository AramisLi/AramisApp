package hello.com.aramis.opengl.douyin.filter

import android.content.Context
import android.opengl.GLES20
import hello.com.aramis.opengl.douyin.utils.OpenGLHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 *Created by Aramis
 *Date:2018/11/2
 *Description:
 */
open class AbstractFilter(val context: Context, vertexShaderId: Int, fragmentShaderId: Int) {

    protected val mGLVertextBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
    protected val mGLTextureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
    protected val mGLProgramId: Int
    protected val vPosition: Int
    protected val vCoord: Int
    protected val vMatrix: Int
    protected val vTextrue: Int
    protected var mOutputWidth: Int = 0
    protected var mOutputHeight: Int = 0

    init {
        val VERTEX = floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f)
        mGLVertextBuffer.clear()
        mGLVertextBuffer.put(VERTEX)

        val TEXTURE = floatArrayOf(0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f)
        mGLTextureBuffer.clear()
        mGLTextureBuffer.put(TEXTURE)

        val vertexSource = OpenGLHelper.readRawTextFile(context, vertexShaderId)
        val fragmentSource = OpenGLHelper.readRawTextFile(context, fragmentShaderId)
        mGLProgramId = OpenGLHelper.loadProgram(vertexSource, fragmentSource)

        //获取变量
        vPosition = GLES20.glGetAttribLocation(mGLProgramId, "vPosition")
        vCoord = GLES20.glGetAttribLocation(mGLProgramId, "vCoord")
        vMatrix = GLES20.glGetUniformLocation(mGLProgramId, "vMatrix")
        vTextrue = GLES20.glGetUniformLocation(mGLProgramId, "vTextrue")

        this.initCoordinate()
    }

    protected open fun initCoordinate() {

    }

    open fun onReady(width: Int, height: Int) {
        mOutputWidth = width
        mOutputHeight = height
    }

    open fun release() {
        GLES20.glDeleteProgram(mGLProgramId)
    }

    open fun onDrawFrame(textureId: Int): Int {
        var ctextureId = textureId

        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight)
        GLES20.glUseProgram(mGLProgramId)
        //传递坐标
        mGLVertextBuffer.position(0)
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mGLVertextBuffer)
        GLES20.glEnableVertexAttribArray(vPosition)

        mGLTextureBuffer.position(0)
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer)
        GLES20.glEnableVertexAttribArray(vCoord)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ctextureId)
        GLES20.glUniform1i(vTextrue, 0)
        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        //解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        return ctextureId
    }

}