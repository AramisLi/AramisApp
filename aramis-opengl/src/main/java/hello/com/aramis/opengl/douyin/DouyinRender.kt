package hello.com.aramis.opengl.douyin

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import hello.com.aramis.opengl.douyin.filter.ScreenFilter
import hello.com.aramis.opengl.douyin.utils.CameraHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *Created by Aramis
 *Date:2018/10/31
 *Description:
 */
class DouyinRender(val mView: DouyinView) : GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private lateinit var mCameraHelper: CameraHelper
    private lateinit var mSurfaceTexture: SurfaceTexture
    private lateinit var mtx: FloatArray
    private lateinit var mScreenFilter: ScreenFilter
    private val mTextures = intArrayOf(0)

    init {

    }

    //绘制方法。类似于onDraw
    override fun onDrawFrame(gl: GL10?) {
        //清理屏幕：告诉opengl，需要把屏幕清理成什么颜色
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        //执行清理
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        //把摄像头的数据显示出来。更新纹理，然后才能使用opengl从SurfaceTexture当中获取数据，进行渲染
        mSurfaceTexture.updateTexImage()

        //surfaceTexture比较特殊，在opengl当中使用的是特殊的采样器  (不是sampler2D)
        //获得变化矩阵
        mSurfaceTexture.getTransformMatrix(mtx)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //开启预览
        mCameraHelper.startPreview(mSurfaceTexture)
        mScreenFilter.onReady(width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //初始化
        mCameraHelper = CameraHelper(Camera.CameraInfo.CAMERA_FACING_BACK)
        //准备摄像头绘制的画布,通过opengl创建一个纹理id
        GLES20.glGenTextures(mTextures.size, mTextures, 0)
        mSurfaceTexture = SurfaceTexture(mTextures[0])
        mSurfaceTexture.setOnFrameAvailableListener(this)

        //必须在gl线程中创建。onSurfaceCreated在gl线程中
        mScreenFilter = ScreenFilter(mView.context)
        mScreenFilter.onDrawFrame(mTextures[0], mtx)
    }

    //SurfaceTexture有一个有效的新数据的时候回调-->让GLSurfaceView去绘制
    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        mView.requestRender()
    }
}