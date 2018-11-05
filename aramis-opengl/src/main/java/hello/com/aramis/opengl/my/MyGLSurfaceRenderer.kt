package hello.com.aramis.opengl.my

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import hello.com.aramis.opengl.R
import java.lang.IllegalStateException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLSurfaceRenderer(private val mView: GLSurfaceView) : GLSurfaceView.Renderer {
    private var program: Int = 0
    private var aPosition: Int = 0
    private lateinit var _pointVFA: FloatBuffer
    private lateinit var _lineVFA: FloatBuffer
    private lateinit var _triangleVFA: FloatBuffer

    override fun onDrawFrame(gl: GL10?) {
        //清除颜色缓冲区默认的颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        GLES20.glUseProgram(program)
        drawPoints()
        drawLine()
        drawTriangle()
    }

    private fun drawTriangle(){
        GLES20.glVertexAttribPointer(aPosition,3,GLES20.GL_FLOAT,false,0,_triangleVFA)
        GLES20.glEnableVertexAttribArray(aPosition)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,3)
    }

    private fun drawLine() {
        GLES20.glVertexAttribPointer(aPosition,3,GLES20.GL_FLOAT,false,0,_lineVFA)
        GLES20.glEnableVertexAttribArray(aPosition)
        GLES20.glLineWidth(10f)//设置线宽度
        GLES20.glDrawArrays(GLES20.GL_LINES,0,2)
    }

    private fun drawPoints() {
        /**
         * indx 参数id
         * size 顶点的取值大小。xyz即为3
         * type 数组的类型
         * normalized: 是否执行标准化操作。暂设为false
         * stride:在float数组中存储单一类型的顶点数据时，例如顶点位置数据，可将stride设置为0或者size与参数类型尺寸间的乘积结果
         * ptr(java.nio.buffer):float缓存数组
         */
        GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 0, _pointVFA)
        GLES20.glEnableVertexAttribArray(aPosition)
        /**
         * mode GLES20.GL_POINTS(点)，GLES20.GL_LINES(线),GL_TRIANGLES(三角)
         * first 从第几个数开始
         * count 数量
         */
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 5)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //像素坐标,xy即为左下角
        GLES20.glViewport(0, 0, width, height)
    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //这句话的主要作用是 对颜色缓冲区内的全部元素进行初始化，即初始化为蓝色。所以这句话可以写在onSurfaceCreated
        GLES20.glClearColor(0f, 0f, 1f, 1f)

        val vertexShader = MyOpenGLHelper.loadShaderFromRAW(GLES20.GL_VERTEX_SHADER, mView.context, R.raw.my_vertex)
        if (!MyOpenGLHelper.checkCreateShaderSuccess(vertexShader)) {
            throw IllegalStateException("创建顶点着色器失败")
        }

        val fragmentShader = MyOpenGLHelper.loadShaderFromRAW(GLES20.GL_FRAGMENT_SHADER, mView.context, R.raw.my_fragment)
        if (!MyOpenGLHelper.checkCreateShaderSuccess(fragmentShader)) {
            throw IllegalStateException("创建片元着色器失败")
        }

        program = MyOpenGLHelper.createProgram(vertexShader, fragmentShader)
        if (!MyOpenGLHelper.checkCreateProgramSuccess(program)) {
            throw IllegalStateException("着色器程序失败")
        }

        aPosition = GLES20.glGetAttribLocation(program, "aPosition")
        val pointVFA = floatArrayOf(
                0.1f, 0.1f, 0f,
                -0.1f, 0.1f, 0f,
                -0.1f, -0.1f, 0f,
                0.1f, -0.1f, 0f,
                0.6f, -0.1f, 0f)
        _pointVFA = ByteBuffer.allocateDirect(pointVFA.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        _pointVFA.put(pointVFA)
        _pointVFA.position(0)

        val lineVFA=floatArrayOf(
                0.0f,0.0f,0.0f,
                0.5f,0.5f,0f
        )

        _lineVFA=ByteBuffer.allocateDirect(pointVFA.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        _lineVFA.put(lineVFA)
        _lineVFA.position(0)

        val triangleVFA=floatArrayOf(
                0.0f,0.0f,0.0f,
                0.5f,0.5f,0f,
                0.7f,0.1f,0f
        )
        _triangleVFA=ByteBuffer.allocateDirect(pointVFA.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        _triangleVFA.put(triangleVFA)
        _triangleVFA.position(0)


    }

}
