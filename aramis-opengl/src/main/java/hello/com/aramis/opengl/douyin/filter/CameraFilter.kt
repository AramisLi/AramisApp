package hello.com.aramis.opengl.douyin.filter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import hello.com.aramis.opengl.R
import hello.com.aramis.opengl.douyin.utils.OpenGLHelper

/**
 *Created by Aramis
 *Date:2018/11/2
 *Description: 不需要显示到屏幕上，用于缓存到FBO中
 */
class CameraFilter(context: Context) : AbstractFilter(context, R.raw.camera_vertex, R.raw.camera_frag) {

    private var mFrameBuffers: IntArray? = null
    private var mFrameBufferTextures: IntArray? = null
    private lateinit var matrix: FloatArray

    override fun initCoordinate() {
        //因为不需要显示到屏幕上，所以要修改成纹理的坐标(左下角为00点)，不是安卓屏幕坐标(左上点是00点)
        mGLTextureBuffer.clear()
        //摄像头的颠倒的(修复了)
        val textureCoordinate = floatArrayOf(0f, 0f, 0f, 1f, 1f, 0f, 1f, 1f)
        mGLTextureBuffer.put(textureCoordinate)
    }

    //gl线程
    override fun onReady(width: Int, height: Int) {
        super.onReady(width, height)
        if (mFrameBuffers != null) {
            destroyFrameBuffers()
        }
        //FBO创建。 Frame Buffer Object缓存
        //1. 创建FBO(离屏屏幕)
        mFrameBuffers = intArrayOf(0)
        GLES20.glGenFramebuffers(1, mFrameBuffers, 0)//参数：创建几个fbo，数量，存放缓存的数组，偏移量
        //2. 创建属于fbo的纹理
        mFrameBufferTextures = intArrayOf(0)
        GLES20.glGenTextures(1, mFrameBufferTextures, 0)
        //3. 配置纹理
        OpenGLHelper.glGenTexture(mFrameBufferTextures!!)
        //4. 绑定
        //4.1 创建一个2d的图像，目标，2d纹理，等级，格式，宽，高，格式，数据类型(byte)+像素数据
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures!![0])
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mOutputWidth, mOutputHeight, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null)
        //4.2 让fbo与纹理绑定起来，后续的操作就是在操作fbo与这个纹理上
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers!![0])
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTextures!![0], 0)
        //5.解绑
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

    override fun onDrawFrame(textureId: Int): Int {
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight)
        //没有使用父类的onDrawFrame是因为默认操作的是GLSurfaceView中的纹理，并显示到屏幕上
        //这里只是把它画到FBO中去
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers!![0])

        GLES20.glUseProgram(mGLProgramId)
        //传递坐标
        mGLVertextBuffer.position(0)
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mGLVertextBuffer)
        GLES20.glEnableVertexAttribArray(vPosition)

        mGLTextureBuffer.position(0)
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer)
        GLES20.glEnableVertexAttribArray(vCoord)

        //变换矩阵赋值，用于旋转图像、镜像等操作
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        GLES20.glUniform1i(vTextrue, 0)
        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        //解绑
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        //返回fbo中的纹理id
        return mFrameBufferTextures!![0]
    }

    fun setMatrix(mtx: FloatArray) {
        this.matrix = mtx
    }

    override fun release() {
        super.release()
        destroyFrameBuffers()
    }


    private fun destroyFrameBuffers() {
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(1, mFrameBufferTextures, 0)
            mFrameBufferTextures = null
        }

        if (mFrameBuffers != null) {
            GLES20.glDeleteBuffers(1, mFrameBuffers, 0)
            mFrameBuffers = null
        }
    }


}