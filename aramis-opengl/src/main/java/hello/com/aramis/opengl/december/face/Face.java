package hello.com.aramis.opengl.december.face;

import java.util.Arrays;

/**
 * Created by Aramis
 * Date:2018/12/6
 * Description:
 */
public class Face {
    //前两位是人脸的x和y，后两个是宽高
    public float[] landmarks;
    public int width;//人脸的宽高
    public int height;

    public int imgWidth;//送去检测图片的宽高
    public int imgHeight;

    public Face(float[] landmarks, int width, int height, int imgWidth, int imgHeight) {
        this.landmarks = landmarks;
        this.width = width;
        this.height = height;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }

    @Override
    public String toString() {
        return "Face{" +
                "landmarks=" + Arrays.toString(landmarks) +
                ", width=" + width +
                ", height=" + height +
                ", imgWidth=" + imgWidth +
                ", imgHeight=" + imgHeight +
                '}';
    }
}
