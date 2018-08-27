package hello.com.aramis.ndk.cmake.utils;

/**
 * Created by Aramis
 * Date:2018/8/6
 * Description:
 */
public class FileBsdiffUtil {
    static {
        System.loadLibrary("bsdiff");
    }

    public native static void fileDiff(String oldFilePath,String newFilePath,String pathFilePath);


    public native static void filePatch(String oldFilePath,String newFilePath,String pathFilePath);
}
