package com.aramis.aramisapp.game.g2048

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.aramis.aramisapp.R
import com.aramis.library.extentions.logE
import com.aramis.library.utils.AramisViewHelper
import org.jetbrains.anko.dip
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 *Created by Aramis
 *Date:2018/10/16
 *Description:
 */
class M2048View : View {

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
        initAnimator()
        whiteTextColor = ContextCompat.getColor(context, R.color.text_white)
        blackTextColor = ContextCompat.getColor(context, R.color.text_black)
        paint.typeface = Typeface.createFromAsset(resources.assets, "ClearSans-Bold.ttf")
        game = M2048(this)

        setOnTouchListener(MyOnTouchListener(this))
        game.startGame()
    }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //Dimensions
    private var cellSize: Float = 0f //单元格长宽
    private var cellDivider: Float = 0f //单元格间隔
    private var gridMargin: Float = 0f //操作区域边距
    private var startingX = 0f//操作区域X
    private var startingY = 0f//操作区域Y
    private var endingX = 0f
    private var endingY = 0f
    //colors
    private val backgroundColor = Color.parseColor("#f0f0f0")
    private var blackTextColor: Int = 0
    private var whiteTextColor: Int = 0
    //Drawables
    private val cellDrawableIds = arrayOf(R.drawable.cell_rectangle, R.drawable.cell_rectangle_2, R.drawable.cell_rectangle_4, R.drawable.cell_rectangle_8, R.drawable.cell_rectangle_16, R.drawable.cell_rectangle_32, R.drawable.cell_rectangle_64, R.drawable.cell_rectangle_128, R.drawable.cell_rectangle_256, R.drawable.cell_rectangle_512, R.drawable.cell_rectangle_1024, R.drawable.cell_rectangle_2048, R.drawable.cell_rectangle_4096)
    private val cellDrawables = mutableListOf<Drawable>()
    private var backgroundBitmap: Bitmap? = null
    //Score
    private var highScore = 100
    private var score = 100
    private val scoreNameTextSize = dip(13).toFloat()
    private val scoreTextSize = dip(22).toFloat()
    //anim
    var animating = false
        private set
    private var animatedValue = 0f
    private var moveDirection = 0
    val moveAnimator = ValueAnimator.ofFloat(0f, 1f)
    private val mergedAnimator = ValueAnimator.ofFloat(0f, 1f)
    private val bornAnimator = ValueAnimator.ofFloat(0f, 1f)

    lateinit var game: M2048
        private set


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //背景
        backgroundBitmap?.apply { canvas.drawBitmap(this, 0f, 0f, paint) }
        //绘制单元格
        drawCells(canvas)
        //绘制测试
        drawTest(canvas)

    }

    private fun drawTest(canvas: Canvas?) {
        paint.color = 0xff333333.toInt()
        var w = 0f
        var h = 0f
        var n = 0
        while (w < width) {
            canvas?.drawLine(cellSize * n, 0f, cellSize * n, height.toFloat(), paint)
            w += cellSize
            n++
        }
        n = 0
        while (h < height) {
            canvas?.drawLine(0f, cellSize * n, width.toFloat(), cellSize * n, paint)
            h += cellSize
            n++
        }

    }

    private var isPaint = false

    private fun drawCells(canvas: Canvas) {
        paint.textSize = dip(25).toFloat()

//        if (!isPaint){
        isPaint = true
        val s = StringBuilder()
        game.handleMatrix.forEachIndexed { index, arrayOfMCells ->
            arrayOfMCells.forEach {
                s.append(it.shortString())
                s.append(",")
            }
            s.append("\n")
        }
//        logE("接受到的：")
//        logE(s.toString())
//        }

        for (arr in game.handleMatrix) {
            arr.forEach { drawCell(canvas, it) }
        }

        drawMergedCells(canvas)

    }

    private fun drawMergedCells(canvas: Canvas) {
        game.mergedCells.forEach {
            val sx = startingX + cellSize * it.y + cellDivider * (it.y + 1)
            val sy = startingY + cellSize * it.x + cellDivider * (it.x + 1)

            val drawable = cellDrawables[sqrt(it.value.toFloat()).toInt()]
            drawable.setBounds(sx.toInt(), sy.toInt(), (sx + cellSize).toInt(), (sy + cellSize).toInt())
            drawable.draw(canvas)
        }
    }

    private fun drawCell(canvas: Canvas, cell: MCell) {
        drawCell(canvas, cell.x, cell.y, cell.movedX, cell.movedY, cell.value)
    }

    private fun drawCell(canvas: Canvas, xx: Int, yy: Int, movedX: Int, movedY: Int, v: Int) {
        val sign = if (moveDirection == M2048.MOVE_RIGHT || moveDirection == M2048.MOVE_DOWN) 1 else -1
        if (v != 0) {
            paint.color = if (v <= 8) blackTextColor else whiteTextColor
            val drawable = cellDrawables[sqrt(v.toFloat()).toInt()]
            val sx = startingX + cellSize * yy + cellDivider * (yy + 1) + (movedY * cellSize * animatedValue + movedY * cellDivider * animatedValue) * sign
            val sy = startingY + cellSize * xx + cellDivider * (xx + 1) + (movedX * cellSize * animatedValue + movedX * cellDivider * animatedValue) * sign
            drawable.setBounds(sx.toInt(), sy.toInt(), (sx + cellSize).toInt(), (sy + cellSize).toInt())
            drawable.draw(canvas)

//            val s = v.toString()
//            val drawTextXY = AramisViewHelper.getDrawTextXY(s, sx + cellSize / 2, sy + cellSize / 2, paint)
//            canvas.drawText(s, drawTextXY[0], drawTextXY[1], paint)
        }
    }

    fun startAnimation(direction: Int) {
        animating = true
        moveDirection = direction
        moveAnimator.start()
    }

    private fun initAnimator() {
        moveAnimator.addUpdateListener {
            animatedValue = it.animatedValue as Float
            invalidate()
        }
        moveAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                animating = false
                logE("onAnimationEnd:$animating")
                game.refreshHandleMatrix(moveDirection)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        moveAnimator.duration = 120
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initDimensions()
        createBackground()
        createCellDrawables()
    }

    private fun createCellDrawables() {

        cellDrawableIds.forEachIndexed { index, item ->
            ContextCompat.getDrawable(context, item)?.apply {
                //                val bitmap=Bitmap.createBitmap()
//                val canvas=Canvas()
//                this.bounds
                val bitmap = Bitmap.createBitmap(cellSize.toInt(), cellSize.toInt(), Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                this.setBounds(0, 0, cellSize.toInt(), cellSize.toInt())
                this.draw(canvas)
                if (index != 0) {
                    val v = 2.0.pow(index).toInt()
                    paint.color = if (v <= 8) blackTextColor else whiteTextColor
                    val drawTextXY = AramisViewHelper.getDrawTextXY(v.toString(), cellSize / 2, cellSize / 2, paint)
                    canvas.drawText(v.toString(), drawTextXY[0], drawTextXY[1], paint)
                }

                val bitmapDrawable = BitmapDrawable(resources, bitmap)
                cellDrawables.add(bitmapDrawable)
            }
        }
    }

    //初始化背景
    private fun createBackground() {
        backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(backgroundBitmap!!)
        canvas.drawColor(backgroundColor)
        //back
        val gameBackDrawable = ContextCompat.getDrawable(context, R.drawable.background_rectangle)
        gameBackDrawable?.setBounds(startingX.toInt(), startingY.toInt(), endingX.toInt(), endingY.toInt())
        gameBackDrawable?.draw(canvas)
        //name
        val nameStringY = cellSize
        val s = 2048.toString()
        paint.textSize = dip(45).toFloat()
        paint.color = ContextCompat.getColor(context, R.color.text_black)
        val rect = Rect()
        paint.getTextBounds(s, 0, s.length, rect)
        val drawTextXY = AramisViewHelper.getDrawTextXY(s, rect.width() / 2 + startingX * 2, rect.height() / 2 + nameStringY, paint)
        canvas.drawText(s, drawTextXY[0], drawTextXY[1], paint)
        //high score
        val highScoreT = "high score"
        val scoreDrawable = ContextCompat.getDrawable(context, R.drawable.score_back)
        val highScoreRight = endingX
        val highScoreLeft = endingX - (cellSize + gridMargin)
        val highScoreBottom = nameStringY + cellSize - gridMargin
        val highScoreHeight = highScoreBottom - nameStringY
        scoreDrawable?.setBounds(highScoreLeft.toInt(), nameStringY.toInt(), highScoreRight.toInt(), highScoreBottom.toInt())
        scoreDrawable?.draw(canvas)
        paint.textSize = scoreNameTextSize
        paint.color = ContextCompat.getColor(context, R.color.text_white)
        val highScoreRectF = RectF(highScoreLeft, nameStringY, highScoreRight, nameStringY + highScoreHeight / 3)
        val highScoreNameXY = AramisViewHelper.getDrawTextXY(highScoreT, highScoreRectF, paint)
        canvas.drawText(highScoreT, highScoreNameXY[0], highScoreNameXY[1], paint)
        val highScoreStr = highScore.toString()
        paint.textSize = scoreTextSize
        highScoreRectF.set(highScoreLeft, nameStringY + highScoreHeight / 4, highScoreRight, highScoreBottom)
        val highScoreTextXY = AramisViewHelper.getDrawTextXY(highScoreStr, highScoreRectF, paint)
        canvas.drawText(highScoreStr, highScoreTextXY[0], highScoreTextXY[1], paint)
        //score
        val scoreT = "score"
        val scoreRight = highScoreLeft - gridMargin
        val scoreLeft = scoreRight - cellSize
        paint.textSize = scoreNameTextSize
        scoreDrawable?.setBounds(scoreLeft.toInt(), nameStringY.toInt(), scoreRight.toInt(), highScoreBottom.toInt())
        scoreDrawable?.draw(canvas)
        highScoreRectF.set(scoreLeft, nameStringY, scoreRight, nameStringY + highScoreHeight / 3)
        val scoreNameXY = AramisViewHelper.getDrawTextXY(scoreT, highScoreRectF, paint)
        canvas.drawText(scoreT, scoreNameXY[0], scoreNameXY[1], paint)

        paint.textSize = scoreTextSize
        highScoreRectF.set(scoreLeft, nameStringY + highScoreHeight / 4, scoreRight, highScoreBottom)
        val scoreTextXY = AramisViewHelper.getDrawTextXY(score.toString(), highScoreRectF, paint)
        canvas.drawText(score.toString(), scoreTextXY[0], scoreTextXY[1], paint)
        //cell back
        val cellBackDrawable = ContextCompat.getDrawable(context, R.drawable.cell_rectangle)
        for (xx in 0 until M2048.numX) {
            for (yy in 0 until M2048.numY) {
                val sx = startingX + cellDivider + (cellSize + cellDivider) * yy
                val sy = startingY + cellDivider + (cellSize + cellDivider) * xx
                val ex = sx + cellSize
                val ey = sy + cellSize
                cellBackDrawable?.setBounds(sx.toInt(), sy.toInt(), ex.toInt(), ey.toInt())
                cellBackDrawable?.draw(canvas)
            }
        }

    }

    private fun initDimensions() {
        cellSize = min(height / (M2048.numX + 2f), width / (M2048.numX + 1f))
        cellDivider = cellSize / 7
        gridMargin = (width - cellSize * M2048.numX - cellDivider * (M2048.numX + 1)) / 2f

        startingX = gridMargin
        startingY = height / 2f - cellSize * 3 / 2
        endingX = startingX + cellSize * M2048.numX + cellDivider * (M2048.numX + 1)
        endingY = startingY + cellSize * M2048.numY + cellDivider * (M2048.numX + 1)

    }


}