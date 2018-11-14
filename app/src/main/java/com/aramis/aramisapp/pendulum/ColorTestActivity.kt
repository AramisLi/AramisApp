package com.aramis.aramisapp.pendulum

import android.os.Bundle
import android.widget.ListView
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import org.jetbrains.anko.listView

/**
 *Created by Aramis
 *Date:2018/10/30
 *Description:
 */
class ColorTestActivity : AraBaseActivity() {
    private val colors = mutableListOf<String>()
    private val colorHelper = ColorHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listView = ListView(this)
        setContentView(listView)

        initColors()
        listView.adapter = ColorTestAdapter(colors)
    }

    private fun initColors() {
        (0..15).forEach {
            val str = "#9BC${colorHelper.toHex(it)}9B"
            colors.add(str)
        }
    }


    override fun getPresenter(): AraBasePresenter<*>? = null
}