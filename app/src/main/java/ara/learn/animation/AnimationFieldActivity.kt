package ara.learn.animation

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.widget.ImageView
import com.aramis.aramisapp.R
import com.aramis.library.extentions.logE
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import kotlinx.android.synthetic.main.activity_anim_view.*

/**
 *Created by Aramis
 *Date:2018/12/17
 *Description:
 */
class AnimationFieldActivity : AraBaseActivity() {
    private lateinit var imageView:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_view)
        imageView=findViewById(R.id.image_test)
        btn_1.setOnClickListener {
            logE("click")
            val set = AnimatorInflater.loadAnimator(this@AnimationFieldActivity, R.animator.property_animator) as AnimatorSet
            set.setTarget(image_test)
            set.start()

        }

        btn_2.setOnClickListener {
            imageView.x=100f
            val objectAnimator=ObjectAnimator.ofFloat(imageView,"x",100f,200f)
            objectAnimator.duration = 1000
            objectAnimator.start()
        }

        btn_3.setOnClickListener {
            val objectAnimator=ObjectAnimator.ofInt(image_test,"translationX",100,200)
            objectAnimator.duration = 1000
            objectAnimator.start()

        }

        btn_4.setOnClickListener {

        }

    }

    override fun getPresenter(): AraBasePresenter<*>? = null
}