package ara.learn.animation

import android.os.Bundle
import android.os.IBinder
import ara.learn.drawable.DrawableAActivity
import ara.learn.ipc.IPCListActivity
import ara.learn.remoteviews.RemoteViewsActivity
import ara.learn.view.ViewListActivity
import com.aramis.aramisapp.R
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import fcom.aramisapp.component.SimpleTextAdapter
import kotlinx.android.synthetic.main.activity_learn_list.*
import org.jetbrains.anko.startActivity

/**
 *Created by Aramis
 *Date:2018/12/17
 *Description:
 */
class AnimationListActivity : AraBaseActivity() {
    private val menuList = mutableListOf("View动画(补间动画)", "帧动画", "属性动画")
    private val adapter = SimpleTextAdapter(menuList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_list)

        list_learn.adapter = adapter
        list_learn.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> startActivity<AnimationViewActivity>()
                1 -> startActivity<AnimationFrameActivity>()
                2 -> startActivity<AnimationFieldActivity>()
            }
        }


        title = "Android动画"
    }


    override fun getPresenter(): AraBasePresenter<*>? = null
}