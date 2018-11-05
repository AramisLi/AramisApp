package hello.com.aramis.opengl.my

import android.content.Context
import android.opengl.GLES20
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 *Created by Aramis
 *Date:2018/11/5
 *Description:
 */
object MyOpenGLHelper {
    private val checkStatus = intArrayOf(0)
    fun loadShaderFromRAW(type: Int, context: Context, sourceRawId: Int): Int {
        val inputStream = context.resources.openRawResource(sourceRawId)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var line = bufferedReader.readLine()
        val stringBuilder = StringBuilder()
        while (line != null) {
            stringBuilder.append(line)
            stringBuilder.append("\n")//这里的\n必须加
            line = bufferedReader.readLine()
        }

        return loadShader(type, stringBuilder.toString())
    }

    /**
     *编译shader
     */
    fun loadShader(type: Int, source: String): Int {
        val shaderId = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shaderId, source)
        GLES20.glCompileShader(shaderId)
        return shaderId
    }

    fun checkCreateShaderSuccess(shader: Int): Boolean {
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, checkStatus, 0)
        return checkStatus[0] == GLES20.GL_TRUE
    }

    fun checkCreateProgramSuccess(progame: Int): Boolean {
        GLES20.glGetProgramiv(progame, GLES20.GL_COMPILE_STATUS, checkStatus, 0)
        return checkStatus[0] == GLES20.GL_TRUE
    }

    fun createProgram(vertexShader: Int, fragmentShader: Int): Int {
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        return program
    }


    fun getProgramFromRAW(context: Context, vertex: Int, fragment: Int): Int {
        val vertexShader = MyOpenGLHelper.loadShaderFromRAW(GLES20.GL_VERTEX_SHADER, context, vertex)
        if (!MyOpenGLHelper.checkCreateShaderSuccess(vertexShader)) {
            throw IllegalStateException("创建顶点着色器失败")
        }

        val fragmentShader = MyOpenGLHelper.loadShaderFromRAW(GLES20.GL_FRAGMENT_SHADER, context, fragment)
        if (!MyOpenGLHelper.checkCreateShaderSuccess(fragmentShader)) {
            throw IllegalStateException("创建片元着色器失败")
        }

        val program = MyOpenGLHelper.createProgram(vertexShader, fragmentShader)
        if (!MyOpenGLHelper.checkCreateProgramSuccess(program)) {
            throw IllegalStateException("着色器程序失败")
        }
        return program
    }
}