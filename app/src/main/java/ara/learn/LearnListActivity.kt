package ara.learn

import android.Manifest
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.IBinder
import ara.learn.activity.IPCActivity
import ara.learn.animation.AnimationListActivity
import ara.learn.drawable.DrawableAActivity
import ara.learn.ipc.IPCListActivity
import ara.learn.ipc.filecache.FileCacheFirstActivity
import ara.learn.remoteviews.RemoteViewsActivity
import ara.learn.view.ViewListActivity
import ara.learn.view.ViewTestActivity
import ara.learn.window.WindowManagerTestActivity
import com.aramis.aramisapp.R
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import fcom.aramisapp.component.SimpleTextAdapter
import kotlinx.android.synthetic.main.activity_learn_list.*
import org.jetbrains.anko.startActivity
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import ara.learn.handler.TestThreadLocalActivity


/**
 *Created by Aramis
 *Date:2018/12/10
 *Description:
 */
class LearnListActivity : AraBaseActivity() {

    private val menuList = mutableListOf("IPC:Inter-Process Communication", "View", "RemoteViews", "Drawable",
            "动画", "Window和WindowManager", "Handler")
    private val adapter = SimpleTextAdapter(menuList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_list)

        list_learn.adapter = adapter
        list_learn.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> startActivity<IPCListActivity>()
                1 -> startActivity<ViewListActivity>()
                2 -> startActivity<RemoteViewsActivity>()
                3 -> startActivity<DrawableAActivity>()
                4 -> startActivity<AnimationListActivity>()
                5 -> {
                    if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {

                        startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")), 1002)
                    } else {
                        startActivity<WindowManagerTestActivity>()
                    }
                }
                6 -> startActivity<TestThreadLocalActivity>()
            }
        }


        val mDeathRecipient = IBinder.DeathRecipient {
        }


        title = "Android Framework"
    }

    override fun getPresenter(): AraBasePresenter<*>? = null
}