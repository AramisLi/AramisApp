package ara.learn.handler;

/**
 * Created by Aramis
 * Date:2018/12/19
 * Description:
 */
public class MyMessageQueue {

    static{
        System.loadLibrary("ara_native");
    }


    public native long nativeInit();

}
