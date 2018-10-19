package com.aramis.aramisapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.aramis.aramisapp.activity.BaiDuMapActivity
import com.aramis.aramisapp.activity.CommonViewsActivity
import com.aramis.aramisapp.game.g2048.G2048Activity
import com.aramis.aramisapp.game.sudoku.SudokuActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.ndkdev.NdkShowListActivity
import fcom.aramisapp.component.SimpleTextAdapter
import hello.com.aramis.ndk.cmake.ui.activity.FileSplitActivity
import hello.com.aramis.ndk.cmake.ui.activity.FmodActivity
import hello.com.aramis.ndk.cmake.utils.AraFileSplitUtil
import hello.com.aramis.ndk.cmake.utils.FmodUtil
import hello.com.aramis.reactnative.ui.NativeActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {
    private val menuList = mutableListOf("NDK", "NDK-CMake", "百度地图", "FMOD 魔音", "自定义View", "React Native", "数独", "2048")
    private val adapter = SimpleTextAdapter(menuList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        setListener()

        toast(FmodUtil.test())
//        toast(FmodUtilJ.test())
    }

    private fun setListener() {
        list_main.setOnItemClickListener { adapterView, view, i, l ->
            when (i) {
                0 -> startActivity<NdkShowListActivity>()

                1 -> startActivity<FileSplitActivity>()
                2 -> startActivity<BaiDuMapActivity>()
                3 -> startActivity<FmodActivity>()
                4 -> startActivity<CommonViewsActivity>()
                5 -> startActivity<NativeActivity>()
                6 -> startActivity<SudokuActivity>()
                7 -> startActivity<G2048Activity>()
            }
        }
        //测试ara-ndk-cmake模块
        btn_ndk_cmake.setOnClickListener {
            val s = AraFileSplitUtil.helloJNI()
            toast(s)
        }

        val flexboxLayoutManager = FlexboxLayoutManager(this)
        flexboxLayoutManager.flexDirection = FlexDirection.COLUMN
        flexboxLayoutManager.justifyContent = JustifyContent.FLEX_END
        recyclerView.layoutManager = flexboxLayoutManager
    }

    private fun initView() {
        list_main.adapter = adapter
    }

}
