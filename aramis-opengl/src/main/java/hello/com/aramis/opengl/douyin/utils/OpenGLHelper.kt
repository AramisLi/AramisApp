package hello.com.aramis.opengl.douyin.utils

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLES20
import com.aramis.library.extentions.logE
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.IllegalStateException

/**
 *Created by Aramis
 *Date:2018/11/1
 *Description:
 */
object OpenGLHelper {
    /**
     * 检查是否支持OpenGL ES 2.0
     */
    fun checkSupport20(context: Context): Boolean =
            getGLESVersion(context) >= 0x20000

    /**
     * 获取设备的OpenGL ES 版本号
     */
    fun getGLESVersion(context: Context): Int =
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).deviceConfigurationInfo.reqGlEsVersion


    /**
     * 获取raw GLSL字符串
     */
    fun readRawTextFile(context: Context, rawId: Int): String {
        val inputStream = context.resources.openRawResource(rawId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        do {
            line = reader.readLine()
            if (line != null) {
                stringBuilder.append(line)
                stringBuilder.append("\n")
            }
        } while (line != null)

        return stringBuilder.toString()
    }

    /**
     * 创建Program程序，并且将顶点着色器和片元着色器与其绑定
     * @return program的ID
     */
    fun loadProgram(vertexSource: String, fragmentSource: String): Int {
        //创建顶点着色器
        val vShaderId = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vShaderId, vertexSource)
        GLES20.glCompileShader(vShaderId)
        val status = intArrayOf(0)
        GLES20.glGetShaderiv(vShaderId, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] != GLES20.GL_TRUE) {
            throw IllegalStateException("Filter 顶点着色器配置失败!")
        }

        //创建片元着色器
        val fShaderId = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fShaderId, fragmentSource)
        GLES20.glCompileShader(fShaderId)
        GLES20.glGetShaderiv(fShaderId, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] != GLES20.GL_TRUE) {
            logE("fragmentSource:$fragmentSource")
            throw IllegalStateException("Filter 片元着色器配置失败!")
        }

        //创建着色器程序(运行在GPU上)
        val programId = GLES20.glCreateProgram()
        GLES20.glAttachShader(programId, vShaderId)
        GLES20.glAttachShader(programId, fShaderId)
        GLES20.glLinkProgram(programId)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, status, 0)
        if (status[0] != GLES20.GL_TRUE) {
            throw IllegalStateException("Filter 着色器程序配置失败!")
        }

        return programId

    }

    /**
     * 创建纹理，并配置
     * @param textures
     */
    fun glGenTexture(textures: IntArray) {
        for (texture in textures) {
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)

            //过滤参数(当纹理被使用到一个比他大或者比他小的形状上时，该如何处理)
            //GL_TEXTURE_MAG_FILTER:比他大时
            //GL_TEXTURE_MIN_FILTER:比他小时
            //GL_LINEAR:线性平均。使用纹理中坐标附近的若干个颜色，通过平均算法进行改变
            //GL_NEAREST:使用纹理坐标最接近的一个颜色作为放大的要绘制的颜色
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)

            //设置纹理的环绕方向(纹理坐标的范围是0-1，如果超出这一范围，OpenGL会根据GL_TEXTURE_WRAP设置的参数来进行处理)
            //GL_TEXTURE_WRAP_S,GL_TEXTURE_WRAP_T 纹理坐标。一般用s和t表示，其实就是xy
            //GL_REPEAT:重复
            //GL_MIRRORED_REPEAT:镜像重复
            //GL_CLAMP_TO_EDGE:坐标超出的部分会被截取成0，1。边缘拉伸
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)


            //解绑
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        }
    }

}