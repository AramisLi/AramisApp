package hello.com.aramis.opengl.december.utils;

import android.content.Context;
import android.opengl.GLES20;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Aramis
 * Date:2018/12/5
 * Description:
 */
public class OpenGLUtils {
    public static void glGenTextures(int[] textures) {
        GLES20.glGenTextures(textures.length, textures, 0);
        for (int i = 0; i < textures.length; i++) {
            //绑定
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i]);
            //过滤参数。当纹理被使用到一个比他打或者比他小的形状上时，该如何处理
            //放大
            // GLES20.GL_LINEAR:使用纹理坐标附近的若干个颜色，通过平均算法，进行放大（线性平均）
            // GL_NEAREST:使用纹理坐标最接近的一个颜色作为放大的颜色
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            //缩小
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);

            /*设置纹理环绕方向*/
            //纹理坐标 一般用st表示，其实就是x y
            //纹理坐标的范围是0-1。超出这一范围的坐标将被OpenGL根据GL_TEXTURE_WRAP参数的值进行处理
            //GL_TEXTURE_WRAP_S, GL_TEXTURE_WRAP_T 分别为x，y方向。
            //GL_REPEAT:平铺
            //GL_MIRRORED_REPEAT: 纹理坐标是奇数时使用镜像平铺
            //GL_CLAMP_TO_EDGE: 坐标超出部分被截取成0、1，边缘拉伸
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //解绑
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
    }

    public static int loadProgram(String vertexSource, String fragSource) {
        //通过字符串(代码)创建着色器程序
        //使用opengl
        //1、创建顶点着色器
        // 1.1
        int vShaderId = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        // 1.2 绑定代码到着色器中去
        GLES20.glShaderSource(vShaderId, vertexSource);
        // 1.3 编译着色器代码
        GLES20.glCompileShader(vShaderId);
        //主动获取成功、失败 (如果不主动查询，只输出 一条 GLERROR之类的日志，很难定位到到底是那里出错)
        int[] status = new int[1];
        GLES20.glGetShaderiv(vShaderId, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            throw new IllegalStateException("ScreenFilter 顶点着色器配置失败!");
        }
        //2、创建片元着色器
        int fShaderId = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fShaderId, fragSource);
        GLES20.glCompileShader(fShaderId);
        GLES20.glGetShaderiv(fShaderId, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            throw new IllegalStateException("ScreenFilter 片元着色器配置失败!");
        }
        //3、创建着色器程序 (GPU上的小程序)
        int mProgram = GLES20.glCreateProgram();
        //把着色器塞到程序当中
        GLES20.glAttachShader(mProgram, vShaderId);
        GLES20.glAttachShader(mProgram, fShaderId);

        //链接着色器
        GLES20.glLinkProgram(mProgram);

        //获得程序是否配置成功
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            throw new IllegalStateException("ScreenFilter 着色器程序配置失败!");
        }

        //因为已经塞到着色器程序中了，所以删了没关系
        GLES20.glDeleteShader(vShaderId);
        GLES20.glDeleteShader(fShaderId);

        return mProgram;
    }

    public static String readRawTextFile(Context context, int id) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            InputStream inputStream = context.getResources().openRawResource(id);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            inputStream.close();
            bufferedReader.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void assets2SDCard(Context context, String fileName, String copyPath) {
        String aPath;
        if (copyPath == null || copyPath.equals("")) {
            aPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
        } else {
            aPath = copyPath + File.separator + fileName;
            File d = new File(copyPath);
            if (!d.exists()) {
                d.mkdirs();
            }
        }

        File file = new File(aPath);

        if (!file.exists()) {
            try {
                InputStream open = context.getAssets().open(fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                int len;
                byte[] buffer = new byte[1024];

                while ((len = open.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                }
                open.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
