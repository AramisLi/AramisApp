package com.aramis.local.pv

import android.os.Environment
import com.aramis.library.extentions.logE
import com.aramis.local.LocalPlayActivity
import fcom.aramisapp.base.AraBasePresenter
import fcom.aramisapp.base.AraBaseView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.File

/**
 *Created by Aramis
 *Date:2018/11/26
 *Description:
 */
class LocalPlayPresenter(view: LocalPlayView) : AraBasePresenter<LocalPlayView>(view) {
    private val videoFileExes = arrayOf("avi", "flv", "mpg", "mpeg", "mpe", "m1v", "m2v", "mpv2", "mp2v", "dat", "ts", "tp", "tpr", "pva", "pss", "mp4", "m4v",
            "m4p", "m4b", "3gp", "3gpp", "3g2", "3gp2", "ogg", "mov", "qt", "amr", "rm", "ram", "rmvb", "rpm")

    fun getData(dataList: MutableList<File>) {
        (activity as? LocalPlayActivity).doAsync {
            dataList.clear()
            val root = File(Environment.getExternalStorageDirectory().absolutePath)
            for (f in root.listFiles()) {
                logE(f.name)
                if (isMediaFile(f)) {
                    dataList.add(f)
                }
            }
            logE("弄完了 11")
            uiThread {
                logE("弄完了")
                mView?.onGetDataFinished()
            }
        }
    }

    private fun isMediaFile(f: File): Boolean {
        var b = false
        if (f.isFile) {
            val name = f.name
            if (name.substring(name.lastIndexOf("."), name.length) in videoFileExes) {
                b = true
            }
        }
        return b
    }
}

interface LocalPlayView : AraBaseView {
    fun onGetDataFinished()
}