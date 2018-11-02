package hello.com.aramis.opengl.douyin.utils

import android.hardware.Camera
import android.os.Handler

/**
 *Created by Aramis
 *Date:2018/11/2
 *Description:
 */
class AutoFocusCallback( private val what: Int = 1) : Camera.AutoFocusCallback {
    var handler: Handler? = null
    private val interval = 1300L
    override fun onAutoFocus(success: Boolean, camera: Camera?) {
        handler?.sendEmptyMessageDelayed(what, interval)
    }
}