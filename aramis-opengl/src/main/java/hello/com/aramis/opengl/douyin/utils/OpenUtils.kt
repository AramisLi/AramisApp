package hello.com.aramis.opengl.douyin.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 *Created by Aramis
 *Date:2018/10/31
 *Description:
 */
object OpenUtils {
    fun readRawTextFile(context: Context, rawId: Int): String {
        val inputStream = context.resources.openRawResource(rawId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        do {
            line = reader.readLine()
            stringBuilder.append(line)
            stringBuilder.append("\n")
        } while (line != null)

        return stringBuilder.toString()
    }

}