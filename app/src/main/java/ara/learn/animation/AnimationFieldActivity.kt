package ara.learn.animation

import android.os.Bundle
import com.aramis.aramisapp.R
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import java.lang.reflect.Field

/**
 *Created by Aramis
 *Date:2018/12/17
 *Description:
 */
class AnimationFieldActivity : AraBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_view)

    }

    override fun getPresenter(): AraBasePresenter<*>? = null
}