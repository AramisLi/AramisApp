package com.ndkdev.utils;

/**
 * Created by Aramis
 * Date:2018/7/27
 * Description:
 */
public class NDKUtilsJ {
    public int paramInt = 1;

    public String testStr = "我是大帅哥";

    static {
        System.loadLibrary("hello-jni");
    }

    public native static String getStringFromC();

    public native static String getChineseString(String chinese);

    public native static String crypt(String str);

    public native static String decrypt(String str);

    public native static Byte[] getByteArray();

    public native static String testException();
}
