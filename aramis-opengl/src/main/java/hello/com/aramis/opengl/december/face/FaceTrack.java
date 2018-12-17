package hello.com.aramis.opengl.december.face;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import hello.com.aramis.opengl.december.utils.CameraHelper;

/**
 * Created by Aramis
 * Date:2018/12/6
 * Description:
 */
public class FaceTrack {

    static {
        System.loadLibrary("native-lib-face");
    }

    private long self;
    private CameraHelper mCameraHelper;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private Face mFace;

    public FaceTrack(String model, String seeta, CameraHelper cameraHelper) {
        mCameraHelper = cameraHelper;
        self = native_create(model, seeta);

        mHandlerThread = new HandlerThread("faceTrack");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                synchronized (FaceTrack.this) {
                    mFace = native_detector(self, (byte[]) msg.obj, mCameraHelper.getCameraId(),
                            CameraHelper.WIDTH, CameraHelper.HEIGHT);
                }
            }
        };
    }

    public void startTrack() {
        native_start(self);
    }

    public void stopTrack() {
        synchronized (this) {
            mHandlerThread.quitSafely();
            mHandler.removeCallbacksAndMessages(null);
            native_stop(self);
            self = 0;
        }

    }

    public void detector(byte[] data) {
        mHandler.removeMessages(11);
        Message message = mHandler.obtainMessage(11);
        message.obj = data;
        mHandler.sendMessage(message);
    }

    public Face getFace() {
        return mFace;
    }


    private native long native_create(String model, String seeta);

    private native void native_start(long self);

    private native void native_stop(long self);


    private native Face native_detector(long self, byte[] data, int cameraId, int width, int
            height);
}
