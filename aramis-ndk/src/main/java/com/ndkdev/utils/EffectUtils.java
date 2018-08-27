package com.ndkdev.utils;

/**
 * Created by Aramis
 * Date:2018/8/17
 * Description:
 */
public class EffectUtils {
    static {
        System.loadLibrary("fmod");
        System.loadLibrary("fmodL");
        System.loadLibrary("qq_voicer");
    }

    public native static void fix(String path,int type);
}
