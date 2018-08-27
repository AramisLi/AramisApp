package hello.com.aramis.ndk.cmake.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import hello.com.aramis.ndk.cmake.R
import org.jetbrains.anko.dip
import kotlin.math.cos
import kotlin.math.sin


/**
 *Created by Aramis
 *Date:2018/8/23
 *Description:
 */
class RecordLoadingView : View {
    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    private var duration = 10
    private var circleWidth = 5
    private var circleColor = 0xffaaddcc.toInt()
    var onAnimFinished: (() -> Unit)? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    private var sweepAngle = 30f
    private var _duration = duration
    private var text = ""
    private var v: ValueAnimator? = null


    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.RecordLoadingView, defStyleAttr, 0)
        duration = typedArray.getInt(R.styleable.RecordLoadingView_duration, 10)
        circleWidth = typedArray.getInt(R.styleable.RecordLoadingView_circleWidth, circleWidth)
        circleColor = typedArray.getColor(R.styleable.RecordLoadingView_circleColor, circleColor)
        typedArray.recycle()


        _duration = duration
        text = duration.toString()
        paint.color = circleColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dip(circleWidth).toFloat()
        circlePaint.color = circleColor

        textPaint.color = circleColor
        textPaint.textSize = dip(25).toFloat()
        initAnim()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val space = paint.strokeWidth / 2
        val cx = width / 2f
        rectF.set(space, space, height.toFloat() - space, width.toFloat() - space)
        canvas?.drawArc(rectF, -90f, sweepAngle, false, paint)
        canvas?.drawCircle(cx, space, space, circlePaint)
        val r = cx - space
        canvas?.drawCircle(cx + r * sin(Math.toRadians(sweepAngle.toDouble()).toFloat()),
                cx - r * cos(Math.toRadians(sweepAngle.toDouble()).toFloat()), space, circlePaint)


        val fm = textPaint.fontMetricsInt
        val measureText = textPaint.measureText(text)
        canvas?.drawText(text, cx - measureText / 2, cx - fm.descent + (fm.bottom - fm.top) / 2, textPaint)

//        circlePaint.color = 0xff000000.toInt()
//        canvas?.drawLine(0f, cx, width.toFloat(), cx, circlePaint)
//        canvas?.drawLine(cx, 0f, cx, width.toFloat(), circlePaint)
    }

    private fun initAnim() {

        var cc = 0f
        v = ValueAnimator.ofFloat(0f, 1f)
        v?.addUpdateListener {
            val value = it.animatedValue as Float
            sweepAngle = 360f * value

            if (value - cc >= 0.1f) {
                cc = value
                _duration--
                text = _duration.toString()
            }
            invalidate()
        }

        v?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                _duration = 0
                text = _duration.toString()
                invalidate()
                onAnimFinished?.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })

        v?.interpolator = LinearInterpolator()
        v?.duration = (duration + 1) * 1000L
    }

    fun startAnim() {

        v?.start()
    }
}