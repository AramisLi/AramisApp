package com.aramis.aramisapp.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import org.jetbrains.anko.dip
import java.util.*
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 *Created by Aramis
 *Date:2018/8/24
 *Description:
 */
class ClockView : View {

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
        paint.strokeWidth=4f

        scalePaint.color = 0xffff0000.toInt()

        val calendar = Calendar.getInstance()
        calendar.time = Date(System.currentTimeMillis())
        hourAngle = calendar.get(Calendar.HOUR) * 30.0
        minuteAngle = calendar.get(Calendar.MINUTE) * 6.0
        secondAngle = calendar.get(Calendar.SECOND) * 6.0
//        val a=calendar.get(Calendar.MINUTE)/12 as Int
        for (i in 0 until calendar.get(Calendar.MINUTE) / 2) {
            hourAngle++
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

//        val space = paint.strokeWidth / 2
        canvas?.drawCircle(width / 2f, width / 2f, width / 2f - paint.strokeWidth / 2, paint)

        //刻度
        drawScale(canvas)
        //时针
        drawHands(canvas)

        //圆心
        canvas?.drawCircle(width / 2f, width / 2f, 10f, paint)
    }

    private fun drawHands(canvas: Canvas?) {
        val HAND_LENGTH_SECOND = width / 2f - divider
        val secondInnerAngle = 2.0f
        val tail = dip(6)
        val secondWidth = dip(6)

        val secondLength = width / 2f - dip(15)

//        val pointAc = width / 2f - divider
        val pointAx = width / 2f + secondLength * sin(Math.toRadians(secondAngle)).toFloat()
        val pointAy = width / 2f - secondLength * cos(Math.toRadians(secondAngle)).toFloat()

        val handLength = secondLength + tail
        val pointBx = pointAx - sqrt(handLength * handLength + secondWidth * secondWidth / 4) * sin(Math.toRadians(secondAngle) - atan((tail / 2) / handLength))
        val pointBy = pointAy + sqrt(handLength * handLength + secondWidth * secondWidth / 4) * cos(Math.toRadians(secondAngle) - atan((tail / 2) / handLength))

        val pointCx = pointBx - secondWidth * cos(Math.toRadians(secondAngle))
        val pointCy = pointBy - secondWidth * sin(Math.toRadians(secondAngle))


//        canvas?.drawCircle(pointAx, pointAy, dip(2).toFloat(), paint)
//        canvas?.drawCircle(pointBx, pointBy, dip(2).toFloat(), paint)
//        canvas?.drawCircle(pointCx, pointCy, dip(2).toFloat(), paint)

//        logE("pointAx:$pointAx,pointAy:$pointAy,pointBx:$pointBx,pointBy:$pointBy,pointCx:$pointCx,pointCy:$pointCy")
        val path = Path()
        path.moveTo(pointAx, pointAy)
        path.lineTo(pointBx.toFloat(), pointBy.toFloat())
        path.lineTo(pointCx.toFloat(), pointCy.toFloat())
        path.close()
        paint.style = Paint.Style.FILL_AND_STROKE
        canvas?.drawPath(path, paint)


        val secondX = width / 2f + secondLength * sin(Math.toRadians(secondAngle)).toFloat()
        val secondY = width / 2f - secondLength * cos(Math.toRadians(secondAngle)).toFloat()

//        logE("secondAngle:$secondAngle,minuteAngle:$minuteAngle,hourAngle:$hourAngle")
        paint.style = Paint.Style.STROKE
        canvas?.drawLine(secondX, secondY, width / 2f, width / 2f, paint)
        canvas?.drawLine(getAngleX(minuteAngle, secondLength - dip(15)), getAngleY(minuteAngle, secondLength - dip(15)),
                width / 2f, width / 2f, paint)
        canvas?.drawLine(getAngleX(hourAngle, secondLength - dip(30)), getAngleY(hourAngle, secondLength - dip(30)),
                width / 2f, width / 2f, paint)
    }

    private fun getAngleX(angle: Double, c: Float): Float {
        return width / 2f + c * sin(Math.toRadians(angle)).toFloat()
    }

    private fun getAngleY(angle: Double, c: Float): Float {
        return width / 2f - c * cos(Math.toRadians(angle)).toFloat()
    }

    private fun drawScale(canvas: Canvas?) {
        for (i in 0 until 12) {
            val angle = 30.0 * i

            val length = if (i % 4 == 0) scale_length_long else scale_length_short

            val pointAc = width / 2f - divider
            val pointAx = width / 2f + pointAc * sin(Math.toRadians(angle)).toFloat()
            val pointAy = width / 2f - pointAc * cos(Math.toRadians(angle)).toFloat()

            val pointBx = pointAx - length * sin(Math.toRadians(angle)).toFloat()
            val pointBy = pointAy + length * cos(Math.toRadians(angle)).toFloat()

//            canvas?.drawPoint(pointAx.toFloat(), pointAy.toFloat(), paint)
//            canvas?.drawCircle(pointAx.toFloat(), pointAy.toFloat(), dip(2).toFloat(), paint)
//            canvas?.drawCircle(pointBx.toFloat(), pointBy.toFloat(), dip(2).toFloat(), paint)

            canvas?.drawLine(pointAx, pointAy, pointBx, pointBy, paint)
        }
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

        anim.duration = 60*1000
        anim.repeatCount = ValueAnimator.INFINITE
        anim.repeatMode = ValueAnimator.RESTART
        anim.interpolator = LinearInterpolator()
        anim.start()
    }
}