package com.aramis.aramisapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ndkdev.NdkShowListActivity
import fcom.aramisapp.component.SimpleTextAdapter
import hello.com.aramis.ndk.cmake.ui.activity.FileSplitActivity
import hello.com.aramis.ndk.cmake.utils.AraFileSplitUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {
    private val menuList = mutableListOf("NDK","NDK-CMake")
    private val adapter = SimpleTextAdapter(menuList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        setListener()
    }

    private fun setListener() {
        list_main.setOnItemClickListener { adapterView, view, i, l ->
            when (i) {
                0 -> startActivity<NdkShowListActivity>()

                1 -> startActivity<FileSplitActivity>()
            }
        }
        //测试ara-ndk-cmake模块
        btn_ndk_cmake.setOnClickListener {
            val s = AraFileSplitUtil.helloJNI()
            toast(s)
        }
    }

    private fun initView() {
        list_main.adapter = adapter
    }

}
