package hello.com.aramis.opengl.douyin.utils

import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import java.lang.Exception


/**
 *Created by Aramis
 *Date:2018/10/31
 *Description:
 */
class CameraHelper(var cameraID: Int) : android.hardware.Camera.PreviewCallback {
    private val TAG = "CameraHelper"
    var WIDTH = 640
    var HEIGHT = 480
    private var mCamera: Camera? = null
    private lateinit var buffer: ByteArray
    var mPreviewCallback: Camera.PreviewCallback? = null
    private lateinit var mSurfaceTexture: SurfaceTexture

    fun switchCamera() {
        cameraID = if (cameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
            Camera.CameraInfo.CAMERA_FACING_FRONT
        } else {
            Camera.CameraInfo.CAMERA_FACING_BACK
        }
        stopPreview()
        startPreview(mSurfaceTexture)
    }

    fun startPreview(surfaceTexture: SurfaceTexture) {
        mSurfaceTexture = surfaceTexture
        try {
            mCamera = Camera.open(cameraID)
            mCamera?.apply {
                //配置Camera属性
                val parameters = this.parameters
                //设置预览数据格式为nv21
                parameters.previewFormat = ImageFormat.NV21
                //设置摄像头的宽高
                parameters.setPreviewSize(WIDTH, HEIGHT)
                //
                this.parameters = parameters
                buffer = ByteArray(WIDTH * HEIGHT * 3 / 2)
                this.addCallbackBuffer(buffer)
                this.setPreviewCallbackWithBuffer(this@CameraHelper)
                //设置预览画面
                this.setPreviewTexture(mSurfaceTexture)
                this.startPreview()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopPreview() {
        mCamera?.apply {
            //移除回调接口
            this.setPreviewCallback(null)
            //停止预览
            this.stopPreview()
            //释放摄像头
            this.release()
            mCamera = null
        }
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        //data数据依然是倒的
        if (mPreviewCallback != null) {
            mPreviewCallback?.onPreviewFrame(data, camera)
        }
        camera?.addCallbackBuffer(buffer)
    }
}