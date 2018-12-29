package ara.learn.animation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.aramis.aramisapp.R
import com.aramis.library.base.SimpleBaseAdapter
import com.aramis.library.base.SimpleBaseAdapterHolder
import kotlinx.android.synthetic.main.activity_anim_view_layout.*

/**
 *Created by Aramis
 *Date:2018/12/18
 *Description:
 */
class AnimationLayoutActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_view)
        title = "View动画的特殊使用场景"

    }

}