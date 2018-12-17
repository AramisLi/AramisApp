package hello.com.aramis.opengl.december;

/**
 * Created by Aramis
 * Date:2018/12/6
 * Description:
 */
public class Test {
    static {
        System.loadLibrary("native-lib-face");
    }

    public static native String test();
}
