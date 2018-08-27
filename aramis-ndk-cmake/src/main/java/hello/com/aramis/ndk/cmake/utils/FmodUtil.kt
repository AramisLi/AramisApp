package hello.com.aramis.ndk.cmake.utils

/**
 *Created by Aramis
 *Date:2018/8/16
 *Description:
 */

object FmodUtil {

    init {
        System.loadLibrary("fmod-use")
    }

    external fun test(): String

    external fun play(path: String):Int

    external fun isPlay():Boolean
    external fun release()
}