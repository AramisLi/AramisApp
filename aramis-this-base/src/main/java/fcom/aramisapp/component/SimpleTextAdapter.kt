package fcom.aramisapp.component

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.aramis.library.base.SimpleBaseAdapter
import com.aramis.library.base.SimpleBaseAdapterHolder
import fcom.aramisapp.base.R

/**
 *Created by Aramis
 *Date:2018/7/27
 *Description:
 */
class SimpleTextAdapter(list: List<String>) : SimpleBaseAdapter<String>(list) {
    override fun initDatas(holder: SimpleBaseAdapterHolder, bean: String, position: Int) {
        (holder as ViewHolder).apply {
            text_adapter_base.text = bean
        }
    }

    override fun itemLayout(): Int = R.layout.adapter_simple_text

    override fun initHolder(convertView: View): SimpleBaseAdapterHolder {
        return ViewHolder(convertView.findViewById(R.id.text_adapter_base))
    }

    private inner class ViewHolder(val text_adapter_base: TextView) : SimpleBaseAdapterHolder()
}

//class SimpleTextAdapter(val list: List<String>) : BaseAdapter() {
//    override fun getView(position: Int, contentView: View?, p2: ViewGroup?): View {
//        var view: View? = contentView
//        if (contentView == null) {
//            view = LayoutInflater.from(p2?.context).inflate(R.layout.adapter_simple_text, null)
//        }
//        val textView = view!!.findViewById<TextView>(R.id.text_adapter_base)
//        textView.text = list[position]
//        return view
//    }
//
//    override fun getItem(position: Int): Any = list[position]
//
//    override fun getItemId(position: Int): Long = position.toLong()
//
//    override fun getCount(): Int = list.size
//
//    private inner class ViewHoder()
//
//}