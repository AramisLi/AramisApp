package hello.com.aramis.opengl.my

import android.opengl.GLSurfaceView
import android.os.Bundle
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter

/**
 *Created by Aramis
 *Date:2018/11/1
 *Description:
 */
class MyTestGLESActivity : AraBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myGLSurfaceView = MySurfaceView(this)
        setContentView(myGLSurfaceView)
    }

    override fun getPresenter(): AraBasePresenter<*>? = null
}