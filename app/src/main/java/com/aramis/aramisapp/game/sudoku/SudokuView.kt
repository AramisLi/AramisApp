package com.aramis.aramisapp.game.sudoku

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import com.aramis.library.extentions.logE
import com.aramis.library.utils.AramisViewHelper
import org.jetbrains.anko.dip
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jetbrains.anko.windowManager
import kotlin.math.floor

/**
 *Created by Aramis
 *Date:2018/9/27
 *Description:
 */
class SudokuView : View {
    private val sudokuUtil = SudokuUtil()
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val selectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val auxiliaryPointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val margin = dip(10).toFloat()
    private val strokeWidth_1 = dip(3).toFloat()
    private val strokeWidth_2 = dip(2).toFloat()
    private val strokeWidth_3 = dip(1).toFloat()

    private val left = margin
    private var right = width - margin
    private val top = margin
    private var bottom = right - left

    private var s_height = bottom - top
    private var s_width = right - left
    private var per_s_height = s_height / 9
    private var per_s_width = s_width / 9

    //    private var originMatrix = listOf(listOf<Int>())
    private val handledMatrix = ArrayList<ArrayList<ViewData>>()
    private val handledAnswerMatrix = mutableListOf(mutableListOf<Int>())
    private var handleX = -1
    private var handleY = -1

    var onSolveSuccessListener: (() -> Unit)? = null
    var onItemClickListener: ((a: Int, b: Int, viewData: ViewData) -> Unit)? = null
    var showAuxiliaryLine = true
    var showAuxiliaryPoint = true
    var checkError = true

    //缓存一套题目
    private var nextRandomMatrix: List<List<Int>>? = null
    private var nextAnswerMatrix: List<List<Int>>? = null
    //可见数
    var seeCount = 25
    private var isFinish = false

    init {
        bgPaint.color = 0xff333333.toInt()
        textPaint.color = 0xff333333.toInt()
        selectPaint.color = 0x88aaddcc.toInt()
        textPaint.textSize = dip(16).toFloat()
        auxiliaryPointPaint.color = 0xFFFFD39B.toInt()

    }

    constructor(context: Context?) : super(context){
        afterInit()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){ afterInit()}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){ afterInit()}

    private fun afterInit(){
        getRandomSudokuQuestion()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (!isFinish && event != null) {
            val x = event.x
            val y = event.y
            if (handledMatrix.isNotEmpty() && event.action == MotionEvent.ACTION_DOWN && x > left && x < right && y > top && y < bottom) {

                handleX = floor((y - top) / per_s_height).toInt()
                handleY = floor((x - left) / per_s_width).toInt()

                handledMatrix.forEach {
                    for (i in it) {
                        i.isSelect = false
                    }
                }
                handledMatrix[handleX][handleY].isSelect = true
                logE("handleX:$handleX,handleY:$handleY,select:${handledMatrix[handleX][handleY]}")

                invalidate()
//                logE("a:$a,b:$b,x:$x,y:$y,left:$left,top:$top")
                onItemClickListener?.invoke(handleX, handleY, handledMatrix[handleX][handleY])
                return true
            }
        }
        return false
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (width > 0) {
            drawBg(canvas)

            drawText(canvas)
        }
    }


    private fun drawText(canvas: Canvas?) {
        for (i in 0 until handledMatrix.size) {

            for (j in 0 until handledMatrix[i].size) {
                val v = handledMatrix[i][j].value.toString()
                if (v == "0") {
                    continue
                }
                val isNPC = handledMatrix[i][j].isNPC
//                textPaint.textSize = if (isNPC) dip(16).toFloat() else dip(14).toFloat()
                textPaint.color = if (isNPC) 0xff333333.toInt() else 0xff777777.toInt()
                val perX = left + per_s_width / 2 + j * per_s_width
                val perY = top + per_s_height / 2 + i * per_s_height
                val arr = AramisViewHelper.getDrawTextXY(v, perX, perY, textPaint)
                canvas?.drawText(v, arr[0], arr[1], textPaint)
            }
        }
//        for (i in 0..8) {
//            canvas?.drawLine(left, top + per_s_height / 2 + i * per_s_height, right, top + per_s_height / 2 + i * per_s_height, bgPaint)
//            canvas?.drawLine(left + per_s_height / 2 + i * per_s_width, top, left + per_s_height / 2 + i * per_s_width, bottom, bgPaint)
//        }
    }

    private fun drawBg(canvas: Canvas?) {

        if (handleX > -1 && handledMatrix[handleX][handleY].isSelect) {
            //显示提示线
            if (handledMatrix[handleX][handleY].value > 0) {
                canvas?.drawRect(left + handleY * per_s_width, top, left + (handleY + 1) * per_s_width, bottom, selectPaint)
                canvas?.drawRect(left, top + handleX * per_s_height, right, top + (handleX + 1) * per_s_height, selectPaint)
            } else {
                auxiliaryPointPaint.color = 0xffFAEBD7.toInt()
                canvas?.drawRect(left + per_s_width * handleY, top + per_s_height * handleX,
                        left + per_s_width * (handleY + 1), top + per_s_height * (handleX + 1), auxiliaryPointPaint)
            }
            //显示提示点
            if (showAuxiliaryPoint) {
                val v = handledMatrix[handleX][handleY].value
                for (i in 0 until handledMatrix.size) {
                    for (j in 0 until handledMatrix[i].size) {
                        if (handledMatrix[i][j].value > 0 && handledMatrix[i][j].value == v) {
                            if ((i == handleX && j != handleY) || (i != handleX && j == handleY)) {
                                auxiliaryPointPaint.color = 0xFFFF5566.toInt()
                            } else {
                                auxiliaryPointPaint.color = 0xFFFFD39B.toInt()
                            }
                            canvas?.drawRect(left + per_s_width * j, top + per_s_height * i,
                                    left + per_s_width * (j + 1), top + per_s_height * (i + 1), auxiliaryPointPaint)
                        }
                    }
                }
            }
            //显示错误提示
            if (checkError) {

            }
        }

        bgPaint.style = Paint.Style.STROKE
        bgPaint.strokeWidth = strokeWidth_1

        canvas?.drawRect(left, top, right, bottom, bgPaint)
        for (i in 1..8) {
            bgPaint.strokeWidth = if (i % 3 == 0) strokeWidth_2 else strokeWidth_3
            canvas?.drawLine(left, top + i * per_s_height, right, top + i * per_s_height, bgPaint)
            canvas?.drawLine(left + i * per_s_width, top, left + i * per_s_width, bottom, bgPaint)
        }

    }


    private val metrics = DisplayMetrics()
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        context.windowManager.defaultDisplay.getMetrics(metrics)
        display.getMetrics(metrics)

        val w = metrics.widthPixels
        right = w - margin
        bottom = top + right - left

        s_height = bottom - top
        s_width = right - left
        per_s_height = s_height / 9
        per_s_width = s_width / 9

        setMeasuredDimension(w, w)
    }

    fun setValue(value: Int) {
        if (!handledMatrix[handleX][handleY].isNPC) {
            handledMatrix[handleX][handleY].value = value
            invalidate()

            isFinish = checkSuccess()
            if (isFinish) {
                logE("=================checkSuccess解谜完成")
                onSolveSuccessListener?.invoke()
            }
        }
    }

    private fun checkSuccess(): Boolean {
        for (i in 0 until handledMatrix.size) {
            for (j in 0 until handledMatrix[i].size) {
                val v = handledMatrix[i][j].value
                logE("v:$v,handledAnswerMatrix[i][j]:${handledAnswerMatrix[i][j]}")
                if (v == 0 || v != handledAnswerMatrix[i][j]) {
                    return false
                }
            }
        }

        return true
    }


    private fun setOriginMatrix(originMatrix: List<List<Int>>, answer: List<List<Int>>? = null) {
        logE("setOriginMatrix_answer:$answer")
        toViewDataList(originMatrix, answer)
        invalidate()
    }

    private fun toViewDataList(originMatrix: List<List<Int>>, answer: List<List<Int>>? = null) {
        logE("handledAnswe_answer1:$answer")
        handledMatrix.clear()
        handledAnswerMatrix.clear()

        for (i in 0 until originMatrix.size) {
            val l = arrayListOf<ViewData>()
            for (j in 0 until originMatrix[i].size) {
                val v = originMatrix[i][j]
                val viewData = ViewData(v, i, j, v > 0)
                l.add(viewData)
            }
            handledMatrix.add(l)
            answer?.apply {
                handledAnswerMatrix.add(answer[i].toMutableList())
            }
        }

        logE("handledAnswe_answer2:$answer")
        logE("handledAnswerMatrix:$handledAnswerMatrix")
    }

    fun getRandomSudokuQuestion() {
        doAsync {
            val p = if (nextRandomMatrix == null) {
                sudokuUtil.getRandomSudokuQuestion(seeCount)
            } else {
                Pair(nextAnswerMatrix!!, nextRandomMatrix!!)
            }
            val answer = p.first
            val question = p.second

            uiThread {
                //                    logE("question")
                logE("question:$question")
                logE("  answer:$answer")
                setOriginMatrix(question, answer)
            }
                doAsync {
            val randomSudokuQuestion = sudokuUtil.getRandomSudokuQuestion(seeCount)
            nextAnswerMatrix = randomSudokuQuestion.first
            nextRandomMatrix = randomSudokuQuestion.second
            logE("nextAnswerMatrix:$nextAnswerMatrix")
            logE("nextRandomMatrix:$nextRandomMatrix")
                }
        }
    }


    data class ViewData(var value: Int, val a: Int, val b: Int, val isNPC: Boolean = false, var isSelect: Boolean = false)
}