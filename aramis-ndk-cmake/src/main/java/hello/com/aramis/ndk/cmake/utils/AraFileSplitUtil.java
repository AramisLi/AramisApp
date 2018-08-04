package hello.com.aramis.ndk.cmake.utils;

/**
 * Created by Aramis
 * Date:2018/8/2
 * Description:
 */
public class AraFileSplitUtil {
    static {
        System.loadLibrary("ara-file-split");
    }

    public native static String helloJNI();

    public native static int fileSplit(String filePath, int split);

    public native static void fileMerge(String newFileName, String[] paths);

    public native static void fileCopy(String filePath,String copyFilePath);
}
