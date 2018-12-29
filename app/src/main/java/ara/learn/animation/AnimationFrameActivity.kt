package ara.learn.animation

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.Animation
import com.aramis.aramisapp.R
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import kotlinx.android.synthetic.main.activity_anim_view.*

/**
 *Created by Aramis
 *Date:2018/12/17
 *Description:
 */
class AnimationFrameActivity : AraBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_view)
        title = "帧动画"

        btn_2.visibility = View.GONE
        btn_3.visibility = View.GONE
        btn_4.visibility = View.GONE
        btn_5.visibility = View.GONE
        btn_6.visibility = View.GONE

        val animationDrawable=ContextCompat.getDrawable(this,R.drawable.animation_drawable) as AnimationDrawable
        image_test.setImageDrawable(animationDrawable)

        btn_1.text="帧动画"
        btn_1.setOnClickListener {
            animationDrawable.start()
        }

    }

    override fun getPresenter(): AraBasePresenter<*>? = null
}