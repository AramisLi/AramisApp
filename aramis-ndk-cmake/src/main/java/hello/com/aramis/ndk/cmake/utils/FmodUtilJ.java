package hello.com.aramis.ndk.cmake.utils;

/**
 * Created by Aramis
 * Date:2018/8/16
 * Description:
 */
public class FmodUtilJ {
    static {
        System.loadLibrary("fmod-use");
    }

    public native static String test();
}
