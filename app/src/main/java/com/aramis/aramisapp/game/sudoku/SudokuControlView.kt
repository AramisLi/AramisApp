package com.aramis.aramisapp.game.sudoku

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.aramis.library.extentions.logE
import com.aramis.library.utils.AramisViewHelper
import org.jetbrains.anko.dip
import org.jetbrains.anko.displayMetrics
import java.text.SimpleDateFormat

/**
 *Created by Aramis
 *Date:2018/9/28
 *Description:
 */
class SudokuControlView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var screenWidth = context.displayMetrics.widthPixels
    private val itemLength = dip(45)
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val timePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val selectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val margin = dip(10).toFloat()
    private val timeHeight = dip(15).toFloat()
    private var timeStr = "00:00:00"

    private val b_divider = dip(6)
    private val b_left = margin
    private val b_top = margin * 3 + timeHeight
    private val b_right = b_left + itemLength * 5 + b_divider * 4
    private val b_bottom = b_top + itemLength * 2 + b_divider
    private val pressColor = 0xffD4D4D4.toInt()
    private var handleA = -1f
    private var handleB = -1f
    private var handleC = -1
    private var handleD = -1
    //时间是否进行
    var isTimeRunning = false
        private set

    var onNumberClickListener: ((number: Int) -> Unit)? = null

    init {
        timePaint.color = 0xff333333.toInt()
        timePaint.strokeWidth = dip(2).toFloat()
        timePaint.textSize = dip(20).toFloat()

        textPaint.color = 0xff333333.toInt()
        textPaint.strokeWidth = dip(2).toFloat()
        textPaint.textSize = dip(20).toFloat()

        bgPaint.style = Paint.Style.STROKE
        bgPaint.strokeWidth = dip(2).toFloat()

        selectPaint.color = pressColor

        timeHandler = Handler(Handler.Callback {
            if (isTimeRunning && it.what == 1) {
                var second = timeStr.substring(timeStr.lastIndexOf(":") + 1, timeStr.length).toInt()
                var minute = timeStr.substring(timeStr.indexOf(":") + 1, timeStr.indexOf(":") + 3).toInt()
                var hour = timeStr.substring(0, timeStr.indexOf(":")).toInt()
                second++
                second = if (second > 59) {
                    minute++
                    0
                } else second
                minute = if (minute > 59) {
                    hour++
                    0
                } else minute
                timeStr = "${if (hour < 10) "0" else ""}$hour:${if (minute < 10) "0" else ""}$minute:${if (second < 10) "0" else ""}$second"
                invalidate()
                timeHandler?.sendEmptyMessageDelayed(1, 1000)
            }
            false
        })

        val a = getSavedTime()
        timeStr = if (a.isNotBlank()) a else "00:00:00"
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        logE("event.action:${event?.action}")
        if (event != null) {
            val x = event.x
            val y = event.y

            if (x > b_left && x < b_right && y > b_top && y < b_bottom) {
//                logE("event.action:${event.action},MotionEvent.ACTION_UP:${MotionEvent.ACTION_UP},MotionEvent.ACTION_MOVE:${MotionEvent.ACTION_MOVE}")
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        for (i in 0..4) {
                            if (x in b_left + itemLength * i + b_divider * i..b_left + itemLength * (i + 1) + b_divider * (i + 1)) {
                                handleA = b_left + itemLength * i + b_divider * i
                                handleC = i
                                break
                            }
                        }
                        handleB = if ((y - b_top) > (b_bottom - b_top) / 2) {
                            handleD = 1
                            b_top + itemLength + b_divider
                        } else {
                            handleD = 0
                            b_top
                        }
//                        logE("handleA:$handleA,handleB:$handleB")
                        invalidate()
                        handleCD()
                    }
                    MotionEvent.ACTION_UP -> {
                        handleA = -1f
                        handleB = -1f
                        invalidate()
                    }
                }

            }
        }
        return true
    }

    private fun handleCD() {
        val n = if (handleD < 1) {
            handleC + 1
        } else {
            handleC + 6
        }
        this.onNumberClickListener?.invoke(if (n >= 10) 0 else n)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawTime(canvas)

        drawNumber(canvas)

    }

    private fun drawNumber(canvas: Canvas?) {
//        canvas?.drawRect(b_left, b_top, b_right, b_bottom , selectPaint)

        if (handleA != -1f) {
            canvas?.drawRect(handleA, handleB, handleA + itemLength, handleB + itemLength, selectPaint)
        }

        for (i in 0..9) {
            val itemTop = if (i > 4) b_top + itemLength + b_divider else b_top
            val itemLeft = b_left + (i % 5) * itemLength + (i % 5) * b_divider
            canvas?.drawRect(itemLeft, itemTop, b_left + (i % 5 + 1) * itemLength + (i % 5) * b_divider, itemTop + itemLength, bgPaint)

            val s = if (i == 9) "删除" else (i + 1).toString()
            textPaint.textSize = if (i == 9) dip(15).toFloat() else dip(20).toFloat()
            val arr = AramisViewHelper.getDrawTextXY(s, itemLeft + itemLength / 2, itemTop + itemLength / 2, textPaint)
            canvas?.drawText(s, arr[0], arr[1], textPaint)
        }
    }

    private fun drawTime(canvas: Canvas?) {
        val x = timePaint.measureText(timeStr) / 2 + margin
        val arr = AramisViewHelper.getDrawTextXY(timeStr, x, margin + timeHeight / 2, timePaint)
        canvas?.drawText(timeStr, arr[0], arr[1], timePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(screenWidth, screenWidth / 2)
    }

    private val timeHandler: Handler? = null

    fun startTime() {
        isTimeRunning = true
        timeHandler?.sendEmptyMessageDelayed(1, 1000)
    }

    fun stopTime() {
        isTimeRunning = false
        saveTime(timeStr)
        timeHandler?.sendEmptyMessage(0)
    }

    fun solveSuccess() {
        stopTime()
        timeStr = "00:00:00"
        getSP().edit().putString("time", timeStr).apply()
    }

    private fun getSavedTime(): String {
        val sp = getSP()
        return sp.getString("time", "") ?: ""
    }

    private fun saveTime(time: String) {
        val sp = getSP()
        sp.edit().putString("time", time).apply()
    }

    private fun getSP() = context.getSharedPreferences("sudoku", Context.MODE_PRIVATE)
}