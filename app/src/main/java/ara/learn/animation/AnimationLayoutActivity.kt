package ara.learn.animation

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.aramis.aramisapp.R
import com.aramis.library.base.SimpleBaseAdapter
import com.aramis.library.base.SimpleBaseAdapterHolder
import kotlinx.android.synthetic.main.activity_anim_view_layout.*
import org.jetbrains.anko.backgroundColor

/**
 *Created by Aramis
 *Date:2018/12/18
 *Description:
 */
class AnimationLayoutActivity : AppCompatActivity() {
    private val dataList = mutableListOf("1", "1", "1", "1", "1")
    private val adapter = ListViewAdapter(dataList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_view_layout)
        title = "View动画的特殊使用场景"

        list_anim_layout.adapter = adapter

        //ListView切换效果
        btn_1.setOnClickListener {
            dataList.add("2")
            adapter.notifyDataSetChanged()
        }

        //Activity切换效果
        btn_2.setOnClickListener {
            startActivity(Intent(this@AnimationLayoutActivity,AnimationLayoutActivity2::class.java))
            overridePendingTransition(R.anim.activity_in,R.anim.activity_out)
        }

    }

    private inner class ListViewAdapter(list: List<String>) : SimpleBaseAdapter<String>(list) {
        override fun initDatas(holder: SimpleBaseAdapterHolder, bean: String, position: Int) {
            (holder as ViewHolder).text_chat.text = bean
            holder.text_chat.backgroundColor = 0xffaaddcc.toInt()
        }

        override fun itemLayout(): Int = R.layout.list_ipc_socket

        override fun initHolder(convertView: View): SimpleBaseAdapterHolder =
                ViewHolder(convertView.findViewById(R.id.text_chat))

        private inner class ViewHolder(val text_chat: TextView) : SimpleBaseAdapterHolder()

    }
}