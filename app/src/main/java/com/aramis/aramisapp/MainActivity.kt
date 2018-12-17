package com.aramis.aramisapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import ara.learn.LearnListActivity
import com.aramis.aramisapp.activity.BaiDuMapActivity
import com.aramis.aramisapp.activity.CommonViewsActivity
import com.aramis.aramisapp.game.g2048.G2048Activity
import com.aramis.aramisapp.game.sudoku.SudokuActivity
import com.aramis.aramisapp.pendulum.PendulumActivity
import com.aramis.local.LocalPlayActivity
import com.aramis.panda.PandaListActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import fcom.aramisapp.component.SimpleTextAdapter
import hello.com.aramis.ndk.cmake.ui.activity.FileSplitActivity
import hello.com.aramis.ndk.cmake.utils.AraFileSplitUtil
import hello.com.aramis.opengl.OpenGLMainActivity
import hello.com.aramis.opengl.d2.Constant
import hello.com.aramis.opengl.december.DecemberOpenGLActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {
    private val menuList = mutableListOf("Android学习", "NDK-CMake", "百度地图", "自定义View", "数独", "2048", "牛顿球", "OpenGL", "FFMpeg直播", "FFMpeg本地", "小抖")
    private val adapter = SimpleTextAdapter(menuList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Constant.init(this.displayMetrics.widthPixels, this.displayMetrics.heightPixels)

        initView()
        setListener()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, 1001)
        } else {
        }

//        toast(FmodUtil.test())
//        toast(FmodUtilJ.test())
    }

    private fun setListener() {
        list_main.setOnItemClickListener { adapterView, view, i, l ->
            when (i) {
                0 -> startActivity<LearnListActivity>()
                1 -> startActivity<FileSplitActivity>()
                2 -> startActivity<BaiDuMapActivity>()
                3 -> startActivity<CommonViewsActivity>()
                4 -> startActivity<SudokuActivity>()
                5 -> startActivity<G2048Activity>()
                6 -> startActivity<PendulumActivity>()
                7 -> startActivity<OpenGLMainActivity>()
                8 -> startActivity<PandaListActivity>()
                9 -> startActivity<LocalPlayActivity>()
                10 -> startActivity<DecemberOpenGLActivity>()

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
