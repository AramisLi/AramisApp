package com.aramis.panda

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.aramis.library.component.adapter.DefaultFrgPagerAdapter
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import kotlinx.android.synthetic.main.activity_panda.*
import org.jetbrains.anko.textColor

/**
 *Created by Aramis
 *Date:2018/11/14
 *Description:
 */
class PandaListActivity : AraBaseActivity() {
    private val tabTextList = mutableListOf<TextView>()
    private val tabLienList = mutableListOf<View>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panda)
        initView()
        setListener()
    }

    private fun setListener() {
        view_tab1.setOnClickListener { onChangeTab(0) }
        view_tab2.setOnClickListener { onChangeTab(1) }
        view_tab3.setOnClickListener { onChangeTab(2) }
    }

    private fun onChangeTab(index: Int) {
        (0..2).forEach {
            tabTextList[it].textColor = 0xff999999.toInt()
            tabLienList[it].visibility = View.GONE
        }
        tabTextList[index].textColor = 0xff333333.toInt()
        tabLienList[index].visibility = View.VISIBLE
        viewPager.currentItem = index
    }

    private fun initView() {
        tabTextList.add(text_tab1)
        tabTextList.add(text_tab2)
        tabTextList.add(text_tab3)
        tabLienList.add(line_tab1)
        tabLienList.add(line_tab2)
        tabLienList.add(line_tab3)

        viewPager.adapter = DefaultFrgPagerAdapter(supportFragmentManager, listOf(PandaPagerFragment1(), PandaPagerFragment2(), PandaPagerFragment3()))
        viewPager.offscreenPageLimit = 3
    }

    override fun getPresenter(): AraBasePresenter<*>? = null
}