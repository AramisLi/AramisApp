package hello.com.aramis.opengl.d2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import hello.com.aramis.opengl.R
import java.lang.Exception

/**
 *Created by Aramis
 *Date:2018/11/1
 *Description:
 */
class MySurfaceView2 : SurfaceView, SurfaceHolder.Callback, Runnable {
    private var canvas: Canvas? = null
    private var isDrawing = false
    private var srcBitmap: Bitmap? = null
    private var bgBitmap: Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun run() {
        while (isDrawing) {
            try {
                canvas = holder.lockCanvas()
                draw(canvas)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }

        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.apply {
//            var srcLeft = 0f
//            var srcTop = 0f
//            while (srcTop < height) {
//                canvas.drawBitmap(srcBitmap!!, srcLeft, srcTop, paint)
//                srcLeft += srcBitmap!!.width
//                if (srcLeft > width) {
//                    srcLeft = 0f
//                    srcTop += srcBitmap!!.height
//                }
//            }
            canvas.drawBitmap(bgBitmap!!,0f,0f,paint)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        isDrawing = false
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        isDrawing = true
        srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_flaver)
        bgBitmap= Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas=Canvas(bgBitmap!!)
        var srcLeft = 0f
        var srcTop = 0f
        while (srcTop < height) {
            canvas.drawBitmap(srcBitmap!!, srcLeft, srcTop, paint)
            srcLeft += srcBitmap!!.width
            if (srcLeft > width) {
                srcLeft = 0f
                srcTop += srcBitmap!!.height
            }
        }
        Thread(this).start()
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        holder.addCallback(this)
        this.isFocusable = true
        this.isFocusableInTouchMode = true
        this.keepScreenOn = true
    }
}