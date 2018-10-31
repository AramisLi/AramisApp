package hello.com.aramis.opengl.douyin

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 *Created by Aramis
 *Date:2018/10/31
 *Description:
 */
class DouyinView : GLSurfaceView {

    fun init(attrs: AttributeSet?) {
        //设置EGL版本
        setEGLContextClientVersion(2)
        setRenderer(DouyinRender(this))
        //RENDERMODE_WHEN_DIRTY:按需渲染
        //RENDERMODE_CONTINUOUSLY:一直渲染
        renderMode= RENDERMODE_WHEN_DIRTY

    }


    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(null)
    }
}