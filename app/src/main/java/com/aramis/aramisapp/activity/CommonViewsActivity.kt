package com.aramis.aramisapp.activity

import android.os.Bundle
import com.aramis.aramisapp.R
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import kotlinx.android.synthetic.main.activity_common_clock.*

/**
 *Created by Aramis
 *Date:2018/8/24
 *Description: 自定义view列表
 */
class CommonViewsActivity : AraBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_clock)
        initView()
        setListener()
    }

    private fun setListener() {
        btn_start.setOnClickListener {
            ferrisView.startAnim()
        }
    }

    private fun initView() {
//        list_default.adapter

        clockView.startAnim()
    }

    override fun getPresenter(): AraBasePresenter<*>? = null
}