package ara.learn.activity

import android.os.Bundle
import com.aramis.aramisapp.R
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import kotlinx.android.synthetic.main.activity_learn_ipc.*
import kotlinx.android.synthetic.main.activity_learn_list.*

/**
 *Created by Aramis
 *Date:2018/12/10
 *Description:
 */
class IPCActivity:AraBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_ipc)

        markdown_ipc.loadMarkdownFromAsset("Android学习.md")
//        markdown_ipc.loadMarkdown("##我怎么这么好看")
    }
    override fun getPresenter(): AraBasePresenter<*>? =null
}