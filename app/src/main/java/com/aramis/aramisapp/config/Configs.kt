package com.aramis.aramisapp.config

import android.os.Environment
import java.io.File

/**
 *Created by Aramis
 *Date:2018/12/10
 *Description:
 */
object Configs {
    val BasePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "AramisApp"
}