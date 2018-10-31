package com.aramis.aramisapp.pendulum

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import com.aramis.library.extentions.logE
import com.aramis.library.utils.AramisViewHelper
import org.jetbrains.anko.dip
import kotlin.math.*

/**
 *Created by Aramis
 *Date:2018/10/23
 *Description:
 */
class PendulumView : View {
    //摆长
    var pendulumLength = dip(280).toFloat()
        set(value) {
            field = value
            for (endPoint in endPoints) {
                endPoint.y = value
            }
            invalidate()
        }
    //摆球颜色
    var pendulumColor = 0xfff5f5f5.toInt()
        set(value) {
            field = value
            invalidate()
        }
    //是否阴影
    var shadowed = true
    var shadowColor = 0xff252525.toInt()
    //摆动最大角度
    private var maxThetaAngle = 0.0
    //摆球个数
    private var count = 7
    //摆球间距
    private val ballDivider = dip(6).toFloat()
    //摆球半径
    private var ballRadius = dip(18).toFloat()

    private val shadowOffsetX = dip(15)
    private val shadowOffsetY = dip(25)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val startPoints = mutableListOf<PointF>()
    private val endPoints = mutableListOf<PointF>()
    private val shadowStartPoints = mutableListOf<PointF>()
    private val shadowEndPoints = mutableListOf<PointF>()

    enum class Mode { Pendulum, Newton }

    var mode = Mode.Newton

    //animation
    private val anim = ValueAnimator.ofFloat(0f, 2f)
    private var isRunning = false
    private var firstTempAngle = 0.0
    private var secondTempAngle = 0.0
    private var oneFourthsChanged = false
    private var threeFourthsChanged = false
    private var startSwingAngle = 0.0
    private var endSwingAngle = 0.0
    private var swipeAngle = 0.0
    private var tempAngle = 0.0
    private var symmetricalBallPosition = 0
    private lateinit var leftRange: IntRange
    private lateinit var rightRange: IntRange
    private var isNormal = false
    private var handBallCount = 0

    var onAnimRunningTouchListener: (() -> Unit)? = null

    private fun init() {
        paint.style = Paint.Style.FILL
        paint.strokeWidth = dip(2).toFloat()
        paint.color = pendulumColor
        initAnim()
    }

    private var actionDownX = 0f
    private var isMoveLeft = false
    private var ballPosition = 0
    private var isBallLeft = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isRunning) {

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    actionDownX = event.x
                    ballPosition = getBallPosition(event.x, event.y)
//                    logE("小球位置：$ballPosition")
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = event.x
                    val y = event.y

                    val pIndex = if (mode == Mode.Pendulum) 0 else ballPosition
                    val distance = AramisViewHelper.getPointDistance(PointF(x, y), startPoints[pIndex])
                    val maxThetaRadians = acos(y / distance).toDouble()
                    maxThetaAngle = Math.toDegrees(maxThetaRadians)

//                    x = event.x + ballPosition * ballRadius * 2
                    isBallLeft = x < startPoints[ballPosition].x
//                    logE("dip(1):${dip(1)},ballPosition:$ballPosition,x:$x,startPoints[ballPosition].x:${startPoints[ballPosition].x}")
                    val sign = if (isBallLeft) -1 else 1

                    when {
                        mode == Mode.Pendulum || count == 1 -> {
                            for (index in 0 until startPoints.size) {
                                endPoints[index].x = (startPoints[index].x + sign * sin(maxThetaRadians) * pendulumLength).toFloat()
                                endPoints[index].y = (cos(maxThetaRadians) * pendulumLength).toFloat()
                            }
                        }

                        mode == Mode.Newton -> {

//                            logE("小球位置：$ballPosition")
                            val range = if (isBallLeft) {
                                0..ballPosition
                            } else {
                                ballPosition until endPoints.size
                            }

                            for (index in 0 until startPoints.size) {
                                if (index in range) {

                                    endPoints[index].x = (startPoints[index].x + sign * sin(maxThetaRadians) * pendulumLength).toFloat()
                                    endPoints[index].y = (cos(maxThetaRadians) * pendulumLength).toFloat()
                                } else {
                                    endPoints[index].x = startPoints[index].x
                                    endPoints[index].y = pendulumLength
                                }
                            }
                        }
                    }

                    for (index in 0 until endPoints.size) {
                        if (endPoints[index].y != pendulumLength) {
                            val sx = shadowStartPoints[index].x + sign *(pendulumLength + shadowOffsetY) * sin(maxThetaRadians).toFloat()
                            val sy = (pendulumLength + shadowOffsetY) * cos(maxThetaRadians).toFloat()
                            shadowEndPoints[index].x = sx
                            shadowEndPoints[index].y = sy
                        }
                    }

                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    isMoveLeft = event.x - actionDownX < 0
                    refreshRadians()
                    startAnimation()
                }
            }

        } else {
            onAnimRunningTouchListener?.invoke()
        }
        return true
    }

    private fun getBallPosition(x: Float, y: Float): Int {
        for (index in 0 until endPoints.size) {
            val endPoint = endPoints[index]
            if (x in endPoint.x - ballRadius..endPoint.x + ballRadius && y in endPoint.y - ballRadius..endPoint.y + ballRadius) {
                return index
            }
        }

        return if (x < width / 2) 0 else endPoints.size - 1
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initDimensions()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isRunning) {
            when (mode) {
                Mode.Pendulum -> onPendulumModeDraw(canvas)
                Mode.Newton -> onNewtonModeDraw(canvas)
            }
        } else {
            paint.style = Paint.Style.FILL
            paint.strokeWidth = dip(2).toFloat()

            for (index in 0 until endPoints.size) {
                val endPoint = endPoints[index]
                drawShadowBalls(canvas, index)
                paint.color = pendulumColor
                canvas.drawCircle(endPoint.x, endPoint.y, ballRadius, paint)
                canvas.drawLine(startPoints[index].x, startPoints[index].y, endPoint.x, endPoint.y, paint)
            }
        }


//        drawTest(canvas)

//        drawTest3(canvas)
    }

    private fun drawTest(canvas: Canvas) {
        paint.color = 0xffaaddcc.toInt()

        for (index in 0 until startPoints.size) {
            val point = startPoints[index]

            canvas.drawLine(point.x, 0f, point.x, height.toFloat(), paint)
        }


        canvas.drawLine(0f, 200f, endPoints[ballPosition].x, 200f, paint)

        if (endPoints[ballPosition].x == startPoints[ballPosition].x) {
            paint.color = 0xffff0000.toInt()
            canvas.drawCircle(endPoints[ballPosition].x, 200f, 20f, paint)
        }
    }

    private fun onNewtonModeDraw(canvas: Canvas) {
        if (((!isMoveLeft && ballPosition == 0) || (isMoveLeft && ballPosition == endPoints.size - 1))) {
            onPendulumModeDraw(canvas)
        } else {

            paint.color = pendulumColor
            paint.style = Paint.Style.FILL
            paint.strokeWidth = dip(2).toFloat()


            for (index in 0 until endPoints.size) {

                if (index in leftRange || index in rightRange) {
                    if (index in leftRange) {
                        val angle = if (isMoveLeft) startSwingAngle - firstTempAngle else endSwingAngle + secondTempAngle

                        drawShadowBalls(canvas, index, angle)
                        paint.color = pendulumColor

                        val x = startPoints[index].x - pendulumLength * sin(Math.toRadians(angle)).toFloat()
                        val y = pendulumLength * cos(Math.toRadians(angle)).toFloat()
                        canvas.drawCircle(x, y, ballRadius, paint)
                        canvas.drawLine(startPoints[index].x, startPoints[index].y, x, y, paint)
                    }

                    if (index in rightRange) {

                        val angle = if (isMoveLeft) startSwingAngle - secondTempAngle else endSwingAngle + firstTempAngle

                        drawShadowBalls(canvas, index, angle)
                        paint.color = pendulumColor

                        val x = startPoints[index].x - pendulumLength * sin(Math.toRadians(angle)).toFloat()
                        val y = pendulumLength * cos(Math.toRadians(angle)).toFloat()

                        canvas.drawCircle(x, y, ballRadius, paint)
                        canvas.drawLine(startPoints[index].x, startPoints[index].y, x, y, paint)

                    }
                } else {
                    drawShadowBalls(canvas, index)
                    paint.color = pendulumColor
                    canvas.drawCircle(startPoints[index].x, pendulumLength, ballRadius, paint)
                    canvas.drawLine(startPoints[index].x, startPoints[index].y, startPoints[index].x, pendulumLength, paint)

                }
            }

        }

    }

    private fun onPendulumModeDraw(canvas: Canvas) {
        if (isRunning) {
            paint.color = pendulumColor
            paint.style = Paint.Style.FILL
            paint.strokeWidth = dip(2).toFloat()
            for (index in 0 until endPoints.size) {
                val angle = if (isMoveLeft) startSwingAngle - tempAngle else endSwingAngle + tempAngle
                drawShadowBalls(canvas, index, angle)
                paint.color = pendulumColor
                val x = startPoints[index].x - pendulumLength * sin(Math.toRadians(angle)).toFloat()
                val y = pendulumLength * cos(Math.toRadians(angle)).toFloat()
                canvas.drawCircle(x, y, ballRadius, paint)
                canvas.drawLine(startPoints[index].x, startPoints[index].y, x, y, paint)
            }
        } else {
            paint.color = pendulumColor
            for (index in 0 until endPoints.size) {
                val endPoint = endPoints[index]
                drawShadowBalls(canvas, index)
                paint.color = pendulumColor
                canvas.drawCircle(endPoint.x, endPoint.y, ballRadius, paint)
                canvas.drawLine(startPoints[index].x, startPoints[index].y, endPoint.x, endPoint.y, paint)
            }
        }
    }

    private fun drawShadowBalls(canvas: Canvas, index: Int, angle: Double? = null) {
        if (shadowed) {
            paint.color = shadowColor
            if (angle == null) {
                canvas.drawCircle(shadowEndPoints[index].x, shadowEndPoints[index].y, ballRadius, paint)
                canvas.drawLine(shadowStartPoints[index].x, shadowStartPoints[index].y, shadowEndPoints[index].x, shadowEndPoints[index].y, paint)
            } else {
                val x = shadowStartPoints[index].x - (pendulumLength + shadowOffsetY) * sin(Math.toRadians(angle)).toFloat()
                val y = (pendulumLength + shadowOffsetY) * cos(Math.toRadians(angle)).toFloat()
                canvas.drawCircle(x, y, ballRadius, paint)
                canvas.drawLine(shadowStartPoints[index].x, shadowStartPoints[index].y, x, y, paint)
            }
        }
    }


    private fun accelerateDecelerate(input: Double): Double {
        return (Math.cos((input + 1) * Math.PI) / 2.0) + 0.5
    }

    fun startAnimation() {
        isRunning = true
        anim.start()
    }

    fun stopAnimation() {

    }


    private fun refreshRadians() {
        symmetricalBallPosition = endPoints.size - 1 - ballPosition
        startSwingAngle = maxThetaAngle
        endSwingAngle = -maxThetaAngle
        swipeAngle = maxThetaAngle * 2


        handBallCount = if (isMoveLeft) ballPosition + 1 else endPoints.size - ballPosition
        val border = if (count % 2 == 0) count / 2 else count / 2 + 1
        isNormal = (isMoveLeft && handBallCount < border) || (!isMoveLeft && handBallCount < border)

//        logE("handBallCount:$handBallCount,isNormal:$isNormal,isMoveLeft:$isMoveLeft")

        leftRange = if (isMoveLeft) 0..ballPosition else 0..symmetricalBallPosition
        rightRange = if (isMoveLeft) symmetricalBallPosition until endPoints.size else ballPosition until endPoints.size
        if (!isNormal) {
            leftRange = if (isMoveLeft) 0..ballPosition else 0..(ballPosition - 1)
            rightRange = if (isMoveLeft) ballPosition + 1 until endPoints.size else ballPosition until endPoints.size
        }

    }

    private fun initAnim() {
        anim.duration = 1200
        anim.addUpdateListener {
            val value = it.animatedValue as Float

            if (mode == Mode.Pendulum || (mode == Mode.Newton && ((!isMoveLeft && ballPosition == 0) || (isMoveLeft && ballPosition == endPoints.size - 1)))) {
                tempAngle = if (value < 1) {
                    swipeAngle * accelerateDecelerate(value.toDouble())
                } else {
                    swipeAngle - swipeAngle * accelerateDecelerate(value - 1.0)
                }
            } else {

                if (value <= 1) {
                    val acc = accelerateDecelerate(value.toDouble())
                    if (value <= 0.5) {
                        firstTempAngle = swipeAngle * acc
                        secondTempAngle = maxThetaAngle
                    } else {
                        if (!oneFourthsChanged && !isNormal) {
                            oneFourthsChanged = true
                            threeFourthsChanged = false

                            leftRange = if (isMoveLeft) 0..(endPoints.size - handBallCount - 1) else 0..(handBallCount - 1)
                            rightRange = if (isMoveLeft) endPoints.size - handBallCount until endPoints.size else handBallCount until endPoints.size

//                            logE("四分之一 leftRange:$leftRange,rightRange:$rightRange")
                        }
                        secondTempAngle = swipeAngle * acc
                        firstTempAngle = maxThetaAngle
                    }
                } else {
                    val acc = accelerateDecelerate(value - 1.0)
                    if (value <= 1.5) {
                        secondTempAngle = swipeAngle - swipeAngle * acc
                        firstTempAngle = maxThetaAngle

                    } else {
                        if (!threeFourthsChanged && !isNormal) {
                            threeFourthsChanged = true
                            oneFourthsChanged = false
                            leftRange = if (isMoveLeft) 0..(ballPosition) else 0..(ballPosition - 1)
                            rightRange = if (isMoveLeft) ballPosition + 1 until endPoints.size - 1 else endPoints.size - handBallCount until endPoints.size

//                            logE("四分之三 leftRange:$leftRange,rightRange:$rightRange")
                        }
                        firstTempAngle = swipeAngle - swipeAngle * acc
                        secondTempAngle = maxThetaAngle
                    }
                }
            }

            invalidate()
        }
        anim.interpolator = LinearInterpolator()
        anim.repeatCount = ValueAnimator.INFINITE
        anim.repeatMode = ValueAnimator.RESTART
    }

    private fun initDimensions() {
        val fDivider = if (mode == Mode.Pendulum) ballDivider / 2 else 0f
        val sDivider = if (mode == Mode.Pendulum) ballDivider else 0f
        if (count <= 1) {
            startPoints.add(PointF(width / 2f, 0f))
            endPoints.add(PointF(width / 2f, pendulumLength))
        } else {
            var inDivide = 0f
            var end = count
            if (count % 2 != 0) {
                inDivide = ballRadius
                end = count - 1
                startPoints.add(PointF(width / 2f, 0f))
                endPoints.add(PointF(width / 2f, pendulumLength))
            }
            (0 until end).forEach {
                val sign = if (it % 2 == 0) -1 else 1
                val temp = (it / 2) * (ballRadius * 2 + sDivider)
                val x = width / 2f + (inDivide + ballRadius + fDivider + temp) * sign
                startPoints.add(PointF(x, 0f))
                endPoints.add(PointF(x, pendulumLength))
            }

            startPoints.sortBy { it.x }
            endPoints.sortBy { it.x }

            startPoints.forEach { shadowStartPoints.add(PointF(it.x + shadowOffsetX, it.y)) }
            endPoints.forEach { shadowEndPoints.add(PointF(it.x + shadowOffsetX, it.y + shadowOffsetY)) }

        }
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
}