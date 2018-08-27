package com.aramis.aramisapp.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.aramis.library.extentions.logE
import com.aramis.library.utils.AramisViewHelper
import org.jetbrains.anko.dip
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 *Created by Aramis
 *Date:2018/8/24
 *Description:
 */
class FerrisWheelView : View {

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val scalePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var scale_length_long = dip(8)
    private var scale_length_short = dip(5)
    private var divider = dip(4)

    private var secondAngle = 0.0
    private var minuteAngle = 0.0
    private var hourAngle = 0.0


    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {


        paint.color = 0xff000000.toInt()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dip(5).toFloat()

        scalePaint.color = 0xffff0000.toInt()

        val calendar = Calendar.getInstance()
        calendar.time = Date(System.currentTimeMillis())
        hourAngle = calendar.get(Calendar.HOUR) * 30.0
        minuteAngle = calendar.get(Calendar.MINUTE) * 6.0
        secondAngle = calendar.get(Calendar.SECOND) * 6.0
//        val a=calendar.get(Calendar.MINUTE)/12 as Int
        for (i in 0 until calendar.get(Calendar.MINUTE) / 12) {
            hourAngle++
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = width / 2f

        //背景圆环
        canvas?.drawCircle(width / 2f, width / 2f, width / 2f - paint.strokeWidth / 2, paint)

        //五角星
//        drawPentacle(canvas, cx, cy, dip(80).toFloat())

        paint.strokeWidth = 2f
        paint.style=Paint.Style.FILL
        AramisViewHelper.drawPentacle(canvas,cx,cy,dip(80).toFloat(),paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dip(5).toFloat()
    }

    private fun drawPentacle(canvas: Canvas?, cx: Float, cy: Float, r: Float) {
        val radians18 = Math.toRadians(18.0)
        val radians54 = Math.toRadians(54.0)
        val radians36 = Math.toRadians(36.0)
        val pointA = PointF(cx, cy - r)
        val pointB = PointF(cx + r * cos(radians18).toFloat(), cy - r * sin(radians18).toFloat())
        val pointC = PointF(cx + r * cos(radians54).toFloat(), cy + r * sin(radians54).toFloat())
        val pointD = PointF(cx - r * cos(radians54).toFloat(), cy + r * sin(radians54).toFloat())
        val pointE = PointF(cx - r * cos(radians18).toFloat(), cy - r * sin(radians18).toFloat())

        val pointF = PointF((cx + (r - r * sin(radians18)) * tan(radians18)).toFloat(),
                (cy - r * sin(radians18)).toFloat())
        val pointJ = PointF((cx - (r - r * sin(radians18)) * tan(radians18)).toFloat(),
                (cy - r * sin(radians18)).toFloat())

        val pointG = PointF((cx + r * cos(radians18) - (r - r * sin(radians18)) * cos(radians36) / cos(radians18)).toFloat(),
                (cy - r + (r - r * sin(radians18)) + (r - r * sin(radians18)) * sin(radians36) / cos(radians18)).toFloat())
        val pointI = PointF((cx - (r * cos(radians18) - (r - r * sin(radians18)) * cos(radians36) / cos(radians18))).toFloat(),
                (cy - r + (r - r * sin(radians18)) + (r - r * sin(radians18)) * sin(radians36) / cos(radians18)).toFloat())

        val pointH = PointF(cx,
                (cy + r * cos(radians18) * tan(radians36) - r * sin(radians18)).toFloat())

        paint.strokeWidth = 2f
        val path = Path()
        arrayOf(pointA, pointF, pointB, pointG, pointC, pointH, pointD, pointI, pointE, pointJ).forEachIndexed { index, p ->
            if (index == 0) {

            }
            when (index) {
                0 -> path.moveTo(p.x, p.y)
                else -> path.lineTo(p.x, p.y)
            }
        }
        path.close()
        paint.style=Paint.Style.FILL
        canvas?.drawPath(path, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dip(5).toFloat()
    }

    private fun drawLine(canvas: Canvas?, a: PointF, b: PointF) {
        canvas?.drawLine(a.x, a.y, b.x, b.y, paint)
    }


    fun startAnim() {
        var temp = 0
        val anim = ValueAnimator.ofInt(0, 59)
        anim.addUpdateListener {
            if (temp != it.animatedValue) {
                temp = it.animatedValue as Int
                secondAngle += 6

                if (secondAngle != 0.0 && secondAngle % 360 == 0.0) {
                    minuteAngle += 6
                    if (minuteAngle % 12 == 0.0) {
                        hourAngle++
                    }
                    if (minuteAngle >= 360) {
                        minuteAngle = 0.0
                    }
                }
                invalidate()
            }
        }

        anim.duration = 60 * 1000
        anim.repeatCount = ValueAnimator.INFINITE
        anim.repeatMode = ValueAnimator.RESTART
        anim.interpolator = LinearInterpolator()
        anim.start()
    }
}