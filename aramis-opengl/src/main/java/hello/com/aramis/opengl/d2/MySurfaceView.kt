package hello.com.aramis.opengl.d2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.aramis.library.extentions.logE
import hello.com.aramis.opengl.R

/**
 *Created by Aramis
 *Date:2018/10/30
 *Description:
 */
class MySurfaceView : SurfaceView, SurfaceHolder.Callback {
    constructor(context: Context?) : super(context){init()}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){init()}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){init()}

    private val paint = Paint()
    private lateinit var drawThread : DrawThread
    private lateinit var bgBmp: Bitmap
    private lateinit var bulletBitmap: Bitmap
    private val explodeBimaps = mutableListOf<Bitmap>()
    private lateinit var bullet: Bullet

    private fun init(){
        this.requestFocus()
        this.isFocusableInTouchMode=true
        this.holder.addCallback(this)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        logE("onDraw bgBmp:$bgBmp")
        canvas.drawBitmap(bgBmp, 0f, 0f, paint)
//        bullet.drawSelf(canvas, paint)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        logE("surfaceChanged")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        logE("surfaceDestroyed")
        drawThread.flag=false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        logE("onMeasure")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        logE("onLayout")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        logE("onSizeChanged height:$height,width:$width")

        bgBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bgBmp)
        val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_flaver)
        logE("srcBitmap height:${srcBitmap.height},width:${srcBitmap.width}")
        var srcLeft = 0f
        var srcTop = 0f
        var times=0
        canvas.drawColor(0xffaaddcc.toInt())
        while (srcTop < height) {
            times++
            canvas.drawBitmap(srcBitmap, srcLeft, srcTop, paint)
            srcLeft += srcBitmap.width
            if (srcLeft > width) {
                srcLeft = 0f
                srcTop += srcBitmap.height
            }
        }

        logE("times $times")
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        logE("surfaceCreated")
        paint.isAntiAlias = true
        logE("1")
//        bulletBitmap = BitmapFactory.decodeResource(this.resources, R.drawable.bullet)
        logE("2")
//        bgBmp = BitmapFactory.decodeResource(resources, R.drawable.bullet)
        drawThread = DrawThread(this)
        logE("3")
        drawThread.start()
        logE("drawThread")
    }
}