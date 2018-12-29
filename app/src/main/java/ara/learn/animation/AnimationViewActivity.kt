package ara.learn.animation

import android.os.Bundle
import android.view.animation.AnimationUtils
import com.aramis.aramisapp.R
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import kotlinx.android.synthetic.main.activity_anim_view.*

/**
 *Created by Aramis
 *Date:2018/12/17
 *Description:
 */
class AnimationViewActivity : AraBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_view)
        title = "View动画"


        //平移
        btn_1.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this@AnimationViewActivity, R.anim.view_translate)
            animation.duration = 1000
            image_test.startAnimation(animation)
        }

        //缩放
        btn_2.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this@AnimationViewActivity, R.anim.view_scale)
            animation.duration = 1000
            image_test.startAnimation(animation)
        }

        //旋转
        btn_3.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this@AnimationViewActivity, R.anim.view_rotate)
            animation.duration = 1000
            image_test.startAnimation(animation)
        }
        //透明度
        btn_4.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this@AnimationViewActivity, R.anim.view_alpha)
            animation.duration = 1000
            image_test.startAnimation(animation)
        }
        //set
        btn_5.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this@AnimationViewActivity, R.anim.view_set)
            animation.duration = 1000
            image_test.startAnimation(animation)
        }

        //自定义
        btn_6.setOnClickListener {
            val centerX = image_test.width / 2f
            val centerY = image_test.height / 2f
            val rotate3DAnimation = Rotate3DAnimation(0f, 60f, centerX, centerY, 1.3f, false)
            rotate3DAnimation.duration = 1000
            image_test.startAnimation(rotate3DAnimation)
        }
    }

    override fun getPresenter(): AraBasePresenter<*>? = null
}