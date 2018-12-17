package ara.learn.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aramis.aramisapp.R
import fcom.aramisapp.component.SimpleTextAdapter
import kotlinx.android.synthetic.main.activity_list.*
import org.jetbrains.anko.startActivity

/**
 *Created by Aramis
 *Date:2018/12/13
 *Description:
 */
class ViewListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val list = mutableListOf("view的移动")
        list_default.adapter = SimpleTextAdapter(list)
        list_default.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> startActivity<ViewTestActivity>()
            }
        }
    }
}