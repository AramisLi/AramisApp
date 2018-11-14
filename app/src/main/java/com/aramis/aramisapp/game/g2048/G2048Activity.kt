package com.aramis.aramisapp.game.g2048

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter

/**
 *Created by Aramis
 *Date:2018/10/10
 *Description:
 */
class G2048Activity : AraBaseActivity() {

    private lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textView = TextView(this)
        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        textView.text = "我是大帅哥"

        //不设置view大小，默认全屏
//        setContentView(G2048View(this))
        setContentView(M2048View(this))
    }

    override fun getPresenter(): AraBasePresenter<*>? = null

}