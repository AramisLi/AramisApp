package com.aramis.library.base

import android.os.Environment
import java.io.File

/**
 *Created by Aramis
 *Date:2018/8/23
 *Description:
 */
object Config {
    val baseFilePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "AramisApp"
}