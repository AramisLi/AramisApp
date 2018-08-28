package com.aramis.aramisapp.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.aramis.library.utils.AramisViewHelper
import org.jetbrains.anko.dip
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

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
    //辐条数量
    private var spokeCount = 18
    //筐数量
    private var basketCount = 6
    private val spokeRadian = Math.toRadians(360.0 / spokeCount)
    private val basketRadian = Math.toRadians(360.0 / basketCount)

    //动画偏移量
    private var offsetAngle = 0.0
    //摩天轮圆心坐标
    private var cx = 0f
    private var cy = 0f
    //摩天轮半径
    private var r = 0f
    private var rInner = 0f

    private val path1 = Path()
    private val path2 = Path()
    private val pathBasket = Path()
    private val pathBasket2 = Path()

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {


        paint.color = 0xff000000.toInt()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dip(5).toFloat()

        scalePaint.color = 0xffff0000.toInt()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        cx = width / 2f
        cy = height / 2f - dip(30)

        r = min(cx, cy) - paint.strokeWidth / 2 - 10
        rInner = r - dip(20)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //背景圆环
        drawBackground(canvas)

        //辐条
        drawSpoke(canvas)

        //筐
        paint.strokeWidth = dip(3).toFloat()
        canvas?.drawCircle(cx, cy, r / 3, paint)
        paint.strokeWidth = 4f
        canvas?.drawCircle(cx, cy, r / 4, paint)
        canvas?.drawCircle(cx, cy, r / 4 - 10, paint)

        paint.color = 0xffff9988.toInt()
        paint.style = Paint.Style.FILL
        for (i in 0 until basketCount) {
            val a = basketRadian * i
            val ax = (cx + r * sin(a + offsetAngle)).toFloat()
            val ay = (cy - r * cos(a + offsetAngle)).toFloat()
            canvas?.drawCircle(ax, ay, 10f, paint)
            drawBasket(canvas, ax, ay, paint)
        }

        //底座
        drawPedestal(canvas, cx, cy)
    }

    private fun drawSpoke(canvas: Canvas?) {
        paint.strokeWidth = 4f
        for (i in 0 until spokeCount) {
            val a = spokeRadian * i
            val ax = (cx + rInner * sin(a + offsetAngle)).toFloat()
            val ay = (cy - rInner * cos(a + offsetAngle)).toFloat()
            canvas?.drawLine(cx, cy, ax, ay, paint)
            val bx = cx + r * sin(a + spokeRadian / 2 + offsetAngle).toFloat()
            val by = cy - r * cos(a + spokeRadian / 2 + offsetAngle).toFloat()
            canvas?.drawLine(ax, ay, bx, by, paint)
            val cxt = cx + r * sin(a - spokeRadian / 2 + offsetAngle).toFloat()
            val cyt = cy - r * cos(a - spokeRadian / 2 + offsetAngle).toFloat()
            canvas?.drawLine(ax, ay, cxt, cyt, paint)
        }
    }

    private fun drawBasket(canvas: Canvas?, cx: Float, cy: Float, paint: Paint) {
        val w = 110
        val h = 50
        val w2 = w / 2
        val round = 12
        pathBasket.reset()
        pathBasket.moveTo(cx, cy)
        pathBasket.lineTo(cx - w2 + round, cy)
        pathBasket.cubicTo(cx - w2 + round, cy,cx - w2,cy, cx - w2, cy + round)
        pathBasket.lineTo(cx - w2, cy + h)

        pathBasket.lineTo(cx - w2 + round, cy + h)
        pathBasket.lineTo(cx - w2 + round, cy + round*2)
        pathBasket.cubicTo(cx - w2 + round, cy + round*2,cx - w2 + round, cy + round,cx - w2 + round*2, cy + round)

        pathBasket.lineTo(cx + w2 - round*2, cy + round)
        pathBasket.cubicTo(cx + w2 - round*2, cy + round,cx + w2 - round, cy + round,cx + w2 - round, cy + round*2)
        pathBasket.lineTo(cx + w2 - round, cy + h)

        pathBasket.lineTo(cx + w2, cy + h)
        pathBasket.lineTo(cx + w2, cy)
        pathBasket.cubicTo(cx + w2,cy+round,cx + w2,cy,cx + w2-round,cy)

//        pathBasket.lineTo(cx + w2, cy + h)
//        pathBasket.lineTo(cx + w2, cy)
        pathBasket.close()
        canvas?.drawPath(pathBasket, paint)

        paint.color=0xff000000.toInt()
        canvas?.drawRect(cx - w2, cy + h, cx + w2, cy + h + 20, paint)
        paint.color = 0xffff9988.toInt()
//        canvas?.drawRect(cx - w2, cy + h+20, cx + w2, cy + h + 60, paint)
        pathBasket2.reset()
        pathBasket2.moveTo(cx - w2,cy + h+20)
        pathBasket2.lineTo(cx - w2,cy + h+20-round)
        pathBasket2.cubicTo(cx - w2,cy + h+60-round,cx - w2,cy + h+60,cx - w2+round,cy + h+60)
        pathBasket2.lineTo(cx + w2-round,cy + h+60)
        pathBasket2.cubicTo(cx + w2-round,cy + h+60,cx + w2,cy + h+60,cx + w2,cy + h+60-round)
        pathBasket2.lineTo(cx + w2,cy+ h+20)
        pathBasket2.close()
        canvas?.drawPath(pathBasket2, paint)
    }

    private fun drawPedestal(canvas: Canvas?, cx: Float, cy: Float) {
        paint.color = 0xfff98327.toInt()
        paint.style = Paint.Style.FILL

        val w = 10
        val l = 300
        val h = height.toFloat() - 35

        path1.moveTo(cx - w, cy)
        path1.lineTo(cx - l, h)
        path1.lineTo(cx - l + w * 2, h)
        path1.lineTo(cx + w, cy)
        path1.close()

        path2.moveTo(cx + w, cy)
        path2.lineTo(cx + l, h)
        path2.lineTo(cx + l - w * 2, h)
        path2.lineTo(cx - w, cy)
        path2.close()

        canvas?.drawPath(path1, paint)
        canvas?.drawPath(path2, paint)

        canvas?.drawRoundRect(cx - l - 50, h - 40, cx + l + 50, h, 10f, 10f, paint)
        canvas?.drawCircle(cx, cy, 50f, paint)

        paint.color = 0xffaaddcc.toInt()
        canvas?.drawCircle(cx, cy, 40f, paint)
        paint.color = 0xff000000.toInt()
        path1.reset()
        path2.reset()
        //五角星
        AramisViewHelper.drawPentacle(canvas, cx, cy, 30f, paint, true)
    }

    private fun drawBackground(canvas: Canvas?) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dip(5).toFloat()
        paint.color = 0xff000000.toInt()

        canvas?.drawCircle(cx, cy, r, paint)
        paint.strokeWidth = 10f
        canvas?.drawCircle(cx, cy, rInner, paint)
    }


    fun startAnim() {
        val rect = Rect()
        rect.set((cx - r).toInt(), (cy - r).toInt(), (cx + r).toInt(), (cy + r).toInt())
        val anim = ValueAnimator.ofFloat(0f, 1f)
        anim.addUpdateListener {
            val value = it.animatedValue as Float
            offsetAngle = 360.0 * value
            invalidate()
        }

        anim.duration = 450 * 1000
        anim.repeatCount = ValueAnimator.INFINITE
        anim.repeatMode = ValueAnimator.RESTART
        anim.interpolator = LinearInterpolator()
        anim.start()

    }
}