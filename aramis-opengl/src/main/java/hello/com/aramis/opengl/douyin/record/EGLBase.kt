package hello.com.aramis.opengl.douyin.record

import android.content.Context
import android.opengl.*
import android.view.Surface
import hello.com.aramis.opengl.douyin.filter.ScreenFilter

/**
 *Created by Aramis
 *Date:2018/11/5
 *Description:EGL配置与录制OpenGL操作
 *
 * @param eglContext GLThread的EGL上下文
 */
class EGLBase(val context: Context, val width: Int, val height: Int, val surface: Surface, val eglContext: EGLContext) {

    private lateinit var mEglDisplay: EGLDisplay

    private lateinit var mEglConfig: EGLConfig
    //录制线程的EGL上下文
    private lateinit var mEGLContext: EGLContext
    private var mEglSurface: EGLSurface

    private var mScreenFilter: ScreenFilter

    init {
        //配置EGL环境
        createEGLSurface()
        val attribList = intArrayOf(EGL14.EGL_NONE)
        //把Surface贴到mEglDisplay
        mEglSurface = EGL14.eglCreateWindowSurface(mEglDisplay, mEglConfig, surface, attribList, 0)

        //绑定当前线程的显示设备及上下文，之后操作OpenGL，就是在这个虚拟显示上操作
        if (!EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEGLContext)) {
            throw java.lang.RuntimeException("eglMakeCurrent failed")
        }

        mScreenFilter = ScreenFilter(context)
        mScreenFilter.onReady(width, height)
    }

    fun draw(texureId: Int, timestamp: Long) {
        //绑定当前线程的显示设备及上下文，之后操作OpenGL，就是在这个虚拟显示上操作
        if (!EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEGLContext)) {
            throw java.lang.RuntimeException("eglMakeCurrent failed")
        }

        mScreenFilter.onDrawFrame(texureId)

        //刷新eglSurface
        EGLExt.eglPresentationTimeANDROID(mEglDisplay,mEglSurface,timestamp)
        //交换数据。
        // EGL的工作模式是双缓存,内部有两个Frame Buffer
        //当EGL将一个fb 显示到屏幕上，另一个就在后台等待opengl交换
        EGL14.eglSwapBuffers(mEglDisplay,mEglSurface)
    }

    //释放
    fun release(){
        EGL14.eglDestroySurface(mEglDisplay,mEglSurface)
        EGL14.eglMakeCurrent(mEglDisplay,EGL14.EGL_NO_SURFACE,EGL14.EGL_NO_SURFACE,EGL14.EGL_NO_CONTEXT)
        EGL14.eglDestroyContext(mEglDisplay,mEGLContext)
        EGL14.eglReleaseThread()
        EGL14.eglTerminate(mEglDisplay)
    }


    private fun createEGLSurface() {
        //创建虚拟显示器
        mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        //初始化显示器
        val version = intArrayOf(2)
        if (!EGL14.eglInitialize(mEglDisplay, version, 0, version, 1)) {
            throw RuntimeException("egInitialize failed")
        }

        val attribList = intArrayOf(
                EGL14.EGL_RED_SIZE, 8,//缓冲区中 红分量的位数
                EGL14.EGL_GREEN_SIZE, 8,//缓冲区中 绿分量的位数
                EGL14.EGL_BLUE_SIZE, 8,//缓冲区中 蓝分量的位数
                EGL14.EGL_ALPHA_SIZE, 8,//缓冲区中 透明度分量的位数
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,//和egl版本又有关
                EGL14.EGL_NONE//结尾，必须
        )

        val configs = arrayOf<EGLConfig>()
        val numConfig = intArrayOf(0)
        //attribList：属性列表
        //configs: 获取的配置（输出参数）
        //numConfig:长度和configs一样就可以了
        if (!EGL14.eglChooseConfig(mEglDisplay, attribList, 0, configs, 0, configs.size, numConfig, 0)) {
            throw IllegalArgumentException("eglChooseConfig#2 failed")
        }

        mEglConfig = configs[0]
        //创建EGL上下文
        val ctx_attrib_list = intArrayOf(
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        )
        //share_context:共享上下文。传入绘制线程(GLThread)中的EGL上下文，达到共享资源的目的
        mEGLContext = EGL14.eglCreateContext(mEglDisplay, mEglConfig, eglContext, ctx_attrib_list, 0)

        //判断创建失败
        if (mEGLContext == EGL14.EGL_NO_CONTEXT) {
            throw IllegalArgumentException("EGL Context Error")
        }

    }
}