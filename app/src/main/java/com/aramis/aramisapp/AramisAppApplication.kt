package com.aramis.aramisapp

import ara.learn.ipc.binderpool.BinderPool
import com.aramis.library.base.BunnyApplication
import org.jetbrains.anko.doAsync

/**
 *Created by Aramis
 *Date:2018/10/31
 *Description:
 */
class AramisAppApplication : BunnyApplication() {
    override fun onCreate() {
        super.onCreate()

       doAsync {
           BinderPool.getInstance(this@AramisAppApplication)
       }

    }

    override fun onTerminate() {
        super.onTerminate()
    }
}