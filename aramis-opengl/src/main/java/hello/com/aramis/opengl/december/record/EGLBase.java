package hello.com.aramis.opengl.december.record;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.view.Surface;

import hello.com.aramis.opengl.december.filer.ScreenFilter;


/**
 * Created by Aramis
 * Date:2018/12/6
 * Description: EGL配置 录制opengl
 */
public class EGLBase {

    private final EGLSurface mEglSurface;
    private final ScreenFilter mScreenFilter;
    private EGLDisplay mEglDisplay;
    private EGLConfig mEglConfig;
    private EGLContext mEGLContext;

    public EGLBase(Context context, int width, int height, Surface surface, EGLContext glContext) {
        //配置EGL环境
        createEGL(glContext);

        int[] attrib_list = {EGL14.EGL_NONE};
        //把Surface贴到 mEglDisplay上。返回EGLSurface-->最终保存的绘制页面
        mEglSurface = EGL14.eglCreateWindowSurface(mEglDisplay, mEglConfig, surface, attrib_list, 0);

        if (!EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
        //绑定当前线程的显示设备及上下文，之后操作openGL，就是在这个虚拟显示上操作
        mScreenFilter = new ScreenFilter(context);
        mScreenFilter.onReady(width, height);
    }

    /**
     * 绘制
     *
     * @param textureId 纹理id
     * @param timestamp 时间戳
     */
    public void draw(int textureId, long timestamp) {
        if (!EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
        mScreenFilter.onDrawFrame(textureId);
        //刷新eglSurface的时间戳
        EGLExt.eglPresentationTimeANDROID(mEglDisplay, mEglSurface, timestamp);
        //交换数据。
        //EGL的工作模式是双缓存模式，内部有两个Frame buffer（fb）
        //当EGL将一个fb显示屏幕上，另一个就在后台等待opengl进行交换
        EGL14.eglSwapBuffers(mEglDisplay, mEglSurface);
    }

    /**
     * 释放
     */
    public void release() {

        EGL14.eglDestroySurface(mEglDisplay, mEglSurface);
        EGL14.eglMakeCurrent(mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT);
        EGL14.eglDestroyContext(mEglDisplay, mEGLContext);
        EGL14.eglReleaseThread();
        EGL14.eglTerminate(mEglDisplay);
    }


    private void createEGL(EGLContext glContext) {
        //创建 虚拟显示器
        mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        //初始化 虚拟显示器
        int[] version = new int[2];
        //marjor 主版本 version[0]
        //minor 副版本 version[1]
        if (!EGL14.eglInitialize(mEglDisplay, version, 0, version, 1)) {
            throw new RuntimeException("eglInitialize failed");
        }

        //egl 根据我们配置的属性来选择一个配置
        int[] attrib_list = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,//根据EGL版本有关 GLSurfaceView 920行
                EGL14.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] num_config = new int[1];
        if (!EGL14.eglChooseConfig(mEglDisplay, attrib_list, 0,
                configs, 0, configs.length, num_config, 0)) {
            throw new IllegalArgumentException("egl创建失败");
        }

        mEglConfig = configs[0];
        // GLSurfaceView 793行
        int[] ctx_attrib_list = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE};
        //3: share_context 共享上下文。传绘制线程（GLThread）中的EGLContext，达到资源共享
        mEGLContext = EGL14.eglCreateContext(mEglDisplay, mEglConfig, glContext, ctx_attrib_list, 0);
        if (mEGLContext == EGL14.EGL_NO_CONTEXT) {
            throw new RuntimeException("EGL Context Error");
        }


    }
}
