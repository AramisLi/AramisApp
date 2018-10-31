package com.aramis.aramisapp.pendulum

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.aramis.aramisapp.R
import com.aramis.library.base.SimpleBaseAdapter
import com.aramis.library.base.SimpleBaseAdapterHolder
import org.jetbrains.anko.backgroundColor

/**
 *Created by Aramis
 *Date:2018/10/30
 *Description:
 */
class ColorTestAdapter(val list: List<String>) : SimpleBaseAdapter<String>(list) {
    private val colorHelper = ColorHelper()
    override fun initDatas(holder: SimpleBaseAdapterHolder, bean: String, position: Int) {
        (holder as ViewHolder).text_color_name.text = list[position]
        (holder as ViewHolder).text_color.backgroundColor = Color.parseColor(list[position])
    }

    override fun itemLayout(): Int = R.layout.adapter_color_test

    override fun initHolder(convertView: View): SimpleBaseAdapterHolder {
        return ViewHolder(convertView.findViewById(R.id.text_color),
                convertView.findViewById(R.id.text_color_name))
    }

    private inner class ViewHolder(val text_color: TextView, val text_color_name: TextView) : SimpleBaseAdapterHolder()
}