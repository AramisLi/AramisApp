package com.aramis.panda

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.aramis.library.extentions.logE
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import kotlinx.android.synthetic.main.activity_panda_room.*
import org.jetbrains.anko.toast

/**
 *Created by Aramis
 *Date:2018/11/15
 *Description:
 */
class PandaRoomActivity : AraBaseActivity() {
    override fun getPresenter(): AraBasePresenter<*>? =null

    private var roomId: String = ""
    private lateinit var dnPlayer: DNPlayer
    private var liveUrl: String = ""
    private var couldPlay = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panda_room)
        getDataFromIntent()
        initView()
        setListener()
    }

    @SuppressLint("SetTextI18n")
    private fun printResult(message:String){
        text_url.text="${text_url.text}\n$message"
    }

    private fun getDataFromIntent() {
        roomId = intent.getStringExtra("roomId") ?: roomId
        liveUrl = intent.getStringExtra("liveUrl") ?: liveUrl
        text_url.text=liveUrl
        text_url.visibility= View.VISIBLE
    }

    private fun setListener() {
        text_start.setOnClickListener {
            logE("点击开始")
        }
        text_stop.setOnClickListener { dnPlayer.stop() }
    }

    private fun initView() {
        dnPlayer = DNPlayer()
        dnPlayer.setSurfaceView(surfaceView)
        dnPlayer.setOnPreparedListener {
            runOnUiThread {
                printResult("可以准备开始播放了")
            }
            logE("OnPreparedListener start")
            dnPlayer.start()
        }

        dnPlayer.setOnErrorListener {
            runOnUiThread {
                logE("出错了: $it")
                printResult("出错了: $it")
            }
        }

        couldPlay = liveUrl.isNotBlank()
        if (!couldPlay) {
            toast("直播地址不存在")
        } else {
            dnPlayer.setDataSource(liveUrl)
        }

    }

    override fun onResume() {
        super.onResume()
        couldPlay = liveUrl.isNotBlank()
        if (couldPlay){
            logE("onResume prepare")
            dnPlayer.prepare()
        }
    }

    override fun onStop() {
        super.onStop()
        dnPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        dnPlayer.release()
    }
}