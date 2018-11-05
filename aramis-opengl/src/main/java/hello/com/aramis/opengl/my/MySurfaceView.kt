package hello.com.aramis.opengl.my

import android.content.Context
import android.graphics.Canvas
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 *Created by Aramis
 *Date:2018/11/5
 *Description:
 */
class MySurfaceView : GLSurfaceView {
    private fun init() {
        this.setEGLContextClientVersion(2)
        this.setRenderer(MyGLSurfaceRenderer(this))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }
}