package ara.learn.animation

import android.os.Bundle
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

        btn_1.setOnClickListener {

        }
    }

    override fun getPresenter(): AraBasePresenter<*>? = null
}