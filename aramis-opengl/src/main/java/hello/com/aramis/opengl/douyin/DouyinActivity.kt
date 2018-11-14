package hello.com.aramis.opengl.douyin

import android.os.Bundle
import com.aramis.library.extentions.logE
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import hello.com.aramis.opengl.R
import hello.com.aramis.opengl.douyin.utils.CameraHelper

/**
 *Created by Aramis
 *Date:2018/11/1
 *Description:
 */
class DouyinActivity : AraBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(DouyinView(this))
        setContentView(R.layout.activity_douyin)

    }

    override fun onResume() {
        super.onResume()

    }

    override fun getPresenter(): AraBasePresenter<*>? = null

}