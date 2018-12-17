package hello.com.aramis.opengl.december.views;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import hello.com.aramis.opengl.december.face.FaceTrack;
import hello.com.aramis.opengl.december.filer.CameraFilter;
import hello.com.aramis.opengl.december.filer.ScreenFilter;
import hello.com.aramis.opengl.december.record.MediaRecord;
import hello.com.aramis.opengl.december.utils.CameraHelper;
import hello.com.aramis.opengl.december.utils.OpenGLUtils;

/**
 * Created by Aramis
 * Date:2018/12/5
 * Description:
 */
public class DouyinRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private ScreenFilter mScreenFilter;
    private CameraFilter mCameraFilter;
    private DouyinView mView;
    private CameraHelper mCameraHelper;
    private SurfaceTexture mSurfaceTexture;
    private float[] mtx = new float[16];
    private int[] mTextures;
    //自己写的video 录制类
    private MediaRecord mMediaRecord;

    private String modelPath;
    private String modelName;

    //人脸追踪
    private FaceTrack mFaceTrack;

    public DouyinRender(DouyinView douyinView) {
        mView = douyinView;
//        modelPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AramisApp" + File.separator + "face";
//        modelName = "lbpcascade_frontalface.xml";
//        OpenGLUtils.assets2SDCard(mView.getContext(), modelName, modelPath);
    }

    /**
     * 画布创建好啦
     *
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //初始化的操作
        mCameraHelper = new CameraHelper(Camera.CameraInfo.CAMERA_FACING_BACK);
//        mCameraHelper.setPreviewCallback(this);

        //准备好摄像头绘制的画布
        //通过opengl创建一个纹理id
        mTextures = new int[1];
        GLES20.glGenTextures(mTextures.length, mTextures, 0);
        mSurfaceTexture = new SurfaceTexture(mTextures[0]);
        //
        mSurfaceTexture.setOnFrameAvailableListener(this);
        //注意：必须在gl线程操作opengl
        mCameraFilter = new CameraFilter(mView.getContext());
        mScreenFilter = new ScreenFilter(mView.getContext());

        //当前(渲染线程)EGL的上下文，供于录制线程，进行资源共享
        EGLContext eglContext = EGL14.eglGetCurrentContext();

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "myDY.mp4";
        mMediaRecord = new MediaRecord(mView.getContext(), path, CameraHelper.HEIGHT, CameraHelper.WIDTH, eglContext);


    }

    /**
     * 画布发生了改变
     *
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //创建跟踪器并启动
        mFaceTrack = new FaceTrack(modelPath + File.separator + modelName, "", mCameraHelper);
        mFaceTrack.startTrack();

        //开启预览
        mCameraHelper.startPreview(mSurfaceTexture);
        mCameraFilter.onReady(width, height);
        mScreenFilter.onReady(width, height);
    }

    /**
     * 开始画画吧
     *
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // 配置屏幕
        //清理屏幕 :告诉opengl 需要把屏幕清理成什么颜色
        GLES20.glClearColor(0, 0, 0, 0);
        //执行上一个：glClearColor配置的屏幕颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // 把摄像头的数据先输出来
        // 更新纹理，然后我们才能够使用opengl从SurfaceTexure当中获得数据 进行渲染
        mSurfaceTexture.updateTexImage();
        //surfaceTexture 比较特殊，在opengl当中 使用的是特殊的采样器 samplerExternalOES （不是sampler2D）
        //获得变换矩阵
        mSurfaceTexture.getTransformMatrix(mtx);

        mCameraFilter.setMatrix(mtx);
        int textureId = mCameraFilter.onDrawFrame(mTextures[0]);
        //加载效果滤镜
        //...
        //最后显示到屏幕上
        mScreenFilter.onDrawFrame(textureId);

        //进行录制
        mMediaRecord.fireFrame(textureId, mSurfaceTexture.getTimestamp());
    }

    /**
     * surfaceTexture 有一个有效的新数据的时候回调
     *
     * @param surfaceTexture
     */
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mView.requestRender();
    }


    public void onSurfaceDestroyed() {
        mFaceTrack.stopTrack();
        mCameraHelper.stopPreview();
    }

    public void autoFocus() {
        mCameraHelper.autoFocus();
    }

    public void startRecord(float speed) {
        mMediaRecord.start(speed);
    }

    public void stopRecord() {
        mMediaRecord.stop();
    }

}
