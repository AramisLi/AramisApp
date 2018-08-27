package com.aramis.aramisapp.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aramis.aramisapp.R
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.MapView
import kotlinx.android.synthetic.main.activity_baidu_map.*


/**
 *Created by Aramis
 *Date:2018/8/16
 *Description:
 */
class BaiDuMapActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SDKInitializer.initialize(applicationContext)
        setContentView(R.layout.activity_baidu_map)

    }

    override fun onResume() {
        super.onResume()
        bmapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        bmapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        bmapView.onDestroy()
    }
}