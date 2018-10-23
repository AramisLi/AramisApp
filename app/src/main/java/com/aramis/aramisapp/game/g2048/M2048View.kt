package com.aramis.aramisapp.game.g2048

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.aramis.aramisapp.R
import com.aramis.library.extentions.toRect
import com.aramis.library.utils.AramisViewHelper
import org.jetbrains.anko.dip
import org.jetbrains.anko.toast
import kotlin.math.log
import kotlin.math.min
import kotlin.math.pow

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
        game.onGameOverListener = {
            state = M2048ViewState.Fail
        }
        game.onGameSuccessListener={
            state = M2048ViewState.Success
        }
        game.onGameFinalWinListener = {
            context.toast("恭喜你，赢得了最后的胜利")
        }
        game.startGame()

    }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //Dimensions
    private var cellSize: Float = 0f //单元格长宽
    private var cellDivider: Float = 0f //单元格间隔
    private var gridMargin: Float = 0f //操作区域边距
    var startingX = 0f//操作区域X
    var startingY = 0f//操作区域Y
    private var endingX = 0f
    private var endingY = 0f
    //colors
    private val backgroundColor = Color.parseColor("#f0f0f0")
    private var blackTextColor: Int = 0
    private var whiteTextColor: Int = 0
    //Drawables
    private val cellDrawableIds = arrayOf(R.drawable.cell_rectangle, R.drawable.cell_rectangle_2, R.drawable.cell_rectangle_4, R.drawable.cell_rectangle_8, R.drawable.cell_rectangle_16, R.drawable.cell_rectangle_32, R.drawable.cell_rectangle_64, R.drawable.cell_rectangle_128, R.drawable.cell_rectangle_256, R.drawable.cell_rectangle_512, R.drawable.cell_rectangle_1024, R.drawable.cell_rectangle_2048, R.drawable.cell_rectangle_4096, R.drawable.cell_rectangle_8192, R.drawable.cell_rectangle_16384, R.drawable.cell_rectangle_32768, R.drawable.cell_rectangle_65536, R.drawable.cell_rectangle_final)
    private val cellDrawables = mutableListOf<Drawable>()
    private var backgroundBitmap: Bitmap? = null
    //Score
    private var highScore = 100
    private var score = 100
    private val scoreNameTextSize = dip(13).toFloat()
    private val scoreTextSize = dip(22).toFloat()
    private var highScoreRectF = RectF()
    private var scoreLeft = 0f
    private var scoreTop = 0f
    private var scoreRight = 0f
    private var scoreBottom = 0f
    private var highScoreLeft = 0f
    private var highScoreRight = 0f
    private var highScoreBottom = 0f
    private var highScoreTop = 0f
    //anim
    private val animDuration = 120L
    var animating = false
        private set
    private var animatedValue = 0f
    val moveAnimator = ValueAnimator.ofFloat(0f, 1f)
    private var moveDirection = 0
    private var mergedAnimatedValue = 0f
    private val bornAnimator = ValueAnimator.ofFloat(0f, 1f)

    //merged
    private var doingMergeAnimator = false
    private val mergedAnimator = ValueAnimator.ofFloat(0f, 2f)
    private val mergedAnimDuration = 220L
    //overlay
    private var successOverlay: BitmapDrawable? = null
    private var failOverlay: BitmapDrawable? = null
    private var workingWidth = 0f
    private var workingHeight = 0f
    val firstButtonRectF = RectF()
    val secondButtonRectF = RectF()
    private val buttonTextSize = dip(17).toFloat()
    private var buttonTint = 0x00FFFFFF.toInt()
    lateinit var game: M2048
        private set

    enum class M2048ViewState { Running, Success, Fail }

    //state
    var state = M2048ViewState.Running

    private val finalModeStr = "FinalMode"


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //背景
        backgroundBitmap?.apply { canvas.drawBitmap(this, 0f, 0f, paint) }
        drawScore(canvas)
        //绘制单元格
        drawCells(canvas)
        //绘制测试
        drawTest(canvas)

        when (state) {
            M2048ViewState.Success -> {
                successOverlay?.apply {
                    this.draw(canvas)
                }
            }
            M2048ViewState.Fail -> {
                failOverlay?.apply {
                    this.draw(canvas)
                }
            }
            else -> {
            }
        }

        if (game.finalMode) {
            paint.textSize = dip(22).toFloat()
            paint.color = ContextCompat.getColor(context, R.color.text_black)
            val drawTextXY = AramisViewHelper.getDrawTextXY(finalModeStr, titleRectF.left + titleRectF.width() / 2, titleRectF.bottom + titleRectF.height() / 2 + dip(10), paint)
            canvas.drawText(finalModeStr, drawTextXY[0], drawTextXY[1], paint)
        }


    }

    private fun drawScore(canvas: Canvas) {
        paint.textSize = scoreTextSize
        paint.color = ContextCompat.getColor(context, R.color.text_white)

        this.score = game.score
        highScoreRectF.set(scoreLeft, scoreTop, scoreRight, scoreBottom)
        val scoreTextXY = AramisViewHelper.getDrawTextXY(score.toString(), highScoreRectF, paint)
        canvas.drawText(score.toString(), scoreTextXY[0], scoreTextXY[1], paint)

        this.highScore = game.highScore
        highScoreRectF.set(highScoreLeft, highScoreTop, highScoreRight, highScoreBottom)
        val highScoreTextXY = AramisViewHelper.getDrawTextXY(highScore.toString(), highScoreRectF, paint)
        canvas.drawText(highScore.toString(), highScoreTextXY[0], highScoreTextXY[1], paint)
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

    private fun drawCells(canvas: Canvas) {
        paint.textSize = dip(45).toFloat()
        for (arr in game.handleMatrix) {
            arr.forEach { drawCell(canvas, it) }
        }

        drawRandomCell(canvas)

        drawMergedCells(canvas)

    }

    private fun drawRandomCell(canvas: Canvas) {

        game.randomAddCell?.apply {
            val length = cellSize / 2f
            val ox = startingX + cellSize * this.y + cellDivider * (this.y + 1)
            val oy = startingY + cellSize * this.x + cellDivider * (this.x + 1)
            val sx = ox + length - length * animatedValue
            val sy = oy + length - length * animatedValue
            val ex = ox + cellSize - length + length * animatedValue
            val ey = oy + cellSize - length + length * animatedValue

            val drawable = getCellDrawable(this.value)
            drawable.setBounds(sx.toInt(), sy.toInt(), (ex).toInt(), (ey).toInt())
            drawable.draw(canvas)
        }
    }

    private fun drawPerMergedCells(canvas: Canvas, cell: MCell) {
        val pp = 0.08f * cellSize
        val ox = startingX + cellSize * cell.y + cellDivider * (cell.y + 1)
        val oy = startingY + cellSize * cell.x + cellDivider * (cell.x + 1)

        val sx = ox + (if (mergedAnimatedValue < 1f) -pp * mergedAnimatedValue else -pp + pp * (mergedAnimatedValue - 1))
        val sy = oy + (if (mergedAnimatedValue < 1f) -pp * mergedAnimatedValue else -pp + pp * (mergedAnimatedValue - 1))
        val ex = ox + cellSize + (if (mergedAnimatedValue < 1f) pp * mergedAnimatedValue else pp - pp * (mergedAnimatedValue - 1))
        val ey = oy + cellSize + (if (mergedAnimatedValue < 1f) pp * mergedAnimatedValue else pp - pp * (mergedAnimatedValue - 1))

        val drawable = getCellDrawable(cell.value)
        drawable.setBounds(sx.toInt(), sy.toInt(), (ex).toInt(), (ey).toInt())
        drawable.draw(canvas)
    }


    private fun drawMergedCells(canvas: Canvas) {
        if (doingMergeAnimator) {
//            val sign = if (mergedAnimatedValue > 1f) -1 else 1
            val pp = 0.07f * cellSize
            game.mergedCells.forEach {
                val ox = startingX + cellSize * it.y + cellDivider * (it.y + 1)
                val oy = startingY + cellSize * it.x + cellDivider * (it.x + 1)

                val sx = ox + (if (mergedAnimatedValue < 1f) -pp * mergedAnimatedValue else -pp + pp * (mergedAnimatedValue - 1))
                val sy = oy + (if (mergedAnimatedValue < 1f) -pp * mergedAnimatedValue else -pp + pp * (mergedAnimatedValue - 1))
                val ex = ox + cellSize + (if (mergedAnimatedValue < 1f) pp * mergedAnimatedValue else pp - pp * (mergedAnimatedValue - 1))
                val ey = oy + cellSize + (if (mergedAnimatedValue < 1f) pp * mergedAnimatedValue else pp - pp * (mergedAnimatedValue - 1))

                val drawable = getCellDrawable(it.value)
                drawable.setBounds(sx.toInt(), sy.toInt(), (ex).toInt(), (ey).toInt())
                drawable.draw(canvas)
            }
        }

    }

    private fun drawCell(canvas: Canvas, cell: MCell) {
//        drawCell(canvas, cell.x, cell.y, cell.movedX, cell.movedY, cell.value)
        val xx = cell.x
        val yy = cell.y
        val v = cell.value

        val sign = if (moveDirection == M2048.MOVE_RIGHT || moveDirection == M2048.MOVE_DOWN) 1 else -1
        if (v != 0) {
            paint.color = if (v <= 8) blackTextColor else whiteTextColor
            val drawable = getCellDrawable(v)
            val sx = startingX + cellSize * yy + cellDivider * (yy + 1) + (cell.movedY * cellSize * animatedValue + cell.movedY * cellDivider * animatedValue) * sign
            val sy = startingY + cellSize * xx + cellDivider * (xx + 1) + (cell.movedX * cellSize * animatedValue + cell.movedX * cellDivider * animatedValue) * sign
            val ex = sx + cellSize
            val ey = sy + cellSize
            drawable.setBounds(sx.toInt(), sy.toInt(), (ex).toInt(), (ey).toInt())
            drawable.draw(canvas)

        }
    }


    private fun getCellDrawable(v: Int): Drawable {
        return cellDrawables[log(v.toFloat(), 2f).toInt()]
    }

    fun startAnimation(direction: Int) {
        animating = true
        moveDirection = direction
        moveAnimator.start()
    }

    fun startMergedAnimation() {
        if (game.mergedCells.isNotEmpty()) {
            doingMergeAnimator = true
            mergedAnimator.start()
        }
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
                animatedValue = 0f
//                logE("onAnimationEnd:$animating")
                game.refreshHandleMatrix(moveDirection)
                startMergedAnimation()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        moveAnimator.duration = animDuration

        mergedAnimator.addUpdateListener {
            mergedAnimatedValue = it.animatedValue as Float
            invalidate()
        }
        mergedAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                doingMergeAnimator = false
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })

        mergedAnimator.duration = mergedAnimDuration
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initDimensions()
        createBackground()
        createCellDrawables()
        createSuccessOverlay()
        createFailOverlay()
    }

    private fun createFailOverlay() {
        val (bitmap, canvas) = getOverlayBack(false)

        failOverlay = BitmapDrawable(resources, bitmap)
        failOverlay?.setBounds(startingX.toInt(), startingY.toInt(), endingX.toInt(), endingY.toInt())
    }

    private fun createSuccessOverlay() {
        val (bitmap, canvas) = getOverlayBack(true)

        successOverlay = BitmapDrawable(resources, bitmap)
        successOverlay?.setBounds(startingX.toInt(), startingY.toInt(), endingX.toInt(), endingY.toInt())
    }

    private fun getOverlayBack(win: Boolean): Pair<Bitmap, Canvas> {
        val bitmap = Bitmap.createBitmap(workingWidth.toInt(), workingHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val back = ContextCompat.getDrawable(context, R.drawable.overlay_back)
        back?.setBounds(0, 0, workingWidth.toInt(), workingHeight.toInt())
        back?.draw(canvas)

        val messageStr = if (win) "You Win!" else "You Lose!"
        paint.textSize = dip(45).toFloat()
        paint.color = 0xff333333.toInt()
        val drawTextXY = AramisViewHelper.getDrawTextXY(messageStr, workingWidth / 2f, workingHeight / 2f, paint)
        canvas.drawText(messageStr, drawTextXY[0], drawTextXY[1] - dip(30), paint)

        val firstButtonStr = if (win) "Continue" else "Reset"
        val firstButton = ContextCompat.getDrawable(context, R.drawable.button_continue)
        firstButton?.bounds = firstButtonRectF.toRect()
        firstButton?.draw(canvas)
        paint.textSize = buttonTextSize
        paint.color = 0xff333333.toInt()
        val continueTextXY = AramisViewHelper.getDrawTextXY(firstButtonStr, firstButtonRectF.left + firstButtonRectF.width() / 2f, firstButtonRectF.top + firstButtonRectF.height() / 2f, paint)
        canvas.drawText(firstButtonStr, continueTextXY[0], continueTextXY[1], paint)

        if (win) {
            val secondButtonStr = "Reset"
            val secondButton = ContextCompat.getDrawable(context, R.drawable.button_continue)
            secondButton?.bounds = firstButtonRectF.toRect()
            secondButton?.draw(canvas)
            val secondTextXY = AramisViewHelper.getDrawTextXY(firstButtonStr, secondButtonRectF.left + secondButtonRectF.width() / 2f, secondButtonRectF.top + secondButtonRectF.height() / 2f, paint)
            canvas.drawText(secondButtonStr, secondTextXY[0], secondTextXY[1], paint)
        }

        return bitmap to canvas
    }

    //重新开始
    private fun reset() {
        this.state = M2048ViewState.Running
        game.reset()
        invalidate()
    }


    fun onFailResetButtonClick(event: MotionEvent) {
        onButtonClick(event, 1) {
            reset()
        }
    }

    fun onSuccessContinueButtonClick(event: MotionEvent) {
        onButtonClick(event, 1) {
            state = M2048ViewState.Running
            invalidate()
        }
    }

    fun onSuccessResetButtonClick(event: MotionEvent) {
        onButtonClick(event, 2) {
            reset()
        }
    }

    //type : 1=first 2=second
    private fun onButtonClick(event: MotionEvent, type: Int, onUp: () -> Unit) {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                onUp.invoke()
            }
            MotionEvent.ACTION_DOWN -> {
                if (type == 1) {
                    //first
                } else {
                    //second
                }
            }
        }

    }


    private fun createCellDrawables() {
        paint.textSize = dip(25).toFloat()
        val last = ContextCompat.getDrawable(context, cellDrawableIds[cellDrawableIds.size - 1])
        (0..M2048.numX * M2048.numY + 1).forEach {
            val drawable = if (it < cellDrawableIds.size) {
                ContextCompat.getDrawable(context, cellDrawableIds[it])
            } else {
                last
            }
            drawable?.apply {
                val bitmap = Bitmap.createBitmap(cellSize.toInt(), cellSize.toInt(), Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                this.setBounds(0, 0, cellSize.toInt(), cellSize.toInt())
                this.draw(canvas)
                val v = 2.0.pow(it).toInt()
                paint.color = if (v <= 8) blackTextColor else whiteTextColor
                val s = v.toString()
                paint.textSize = when {
                    s.length > 5 -> dip(18).toFloat()
                    s.length > 4 -> dip(22).toFloat()
                    else -> dip(25).toFloat()
                }
                val drawTextXY = AramisViewHelper.getDrawTextXY(s, cellSize / 2, cellSize / 2, paint)
                canvas.drawText(s, drawTextXY[0], drawTextXY[1], paint)

                val bitmapDrawable = BitmapDrawable(resources, bitmap)
                cellDrawables.add(bitmapDrawable)
            }
        }
//        cellDrawableIds.forEachIndexed { index, item ->
//            ContextCompat.getDrawable(context, item)?.apply {
//                val bitmap = Bitmap.createBitmap(cellSize.toInt(), cellSize.toInt(), Bitmap.Config.ARGB_8888)
//                val canvas = Canvas(bitmap)
//                this.setBounds(0, 0, cellSize.toInt(), cellSize.toInt())
//                this.draw(canvas)
//                if (index != 0) {
//                    val v = 2.0.pow(index).toInt()
//                    paint.color = if (v <= 8) blackTextColor else whiteTextColor
//                    val drawTextXY = AramisViewHelper.getDrawTextXY(v.toString(), cellSize / 2, cellSize / 2, paint)
//                    canvas.drawText(v.toString(), drawTextXY[0], drawTextXY[1], paint)
//                }
//
//                val bitmapDrawable = BitmapDrawable(resources, bitmap)
//                cellDrawables.add(bitmapDrawable)
//            }
//        }
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
        titleRectF.set(startingX * 2, nameStringY, startingX * 2 + rect.width(), rect.height() + nameStringY)
        canvas.drawText(s, drawTextXY[0], drawTextXY[1], paint)
        //high score
        val highScoreT = "high score"
        val scoreDrawable = ContextCompat.getDrawable(context, R.drawable.score_back)
        highScoreRight = endingX
        highScoreLeft = endingX - (cellSize + gridMargin)
        highScoreBottom = nameStringY + cellSize - gridMargin
        val highScoreHeight = highScoreBottom - nameStringY
        highScoreTop = nameStringY + highScoreHeight / 4
        scoreDrawable?.setBounds(highScoreLeft.toInt(), nameStringY.toInt(), highScoreRight.toInt(), highScoreBottom.toInt())
        scoreDrawable?.draw(canvas)
        paint.textSize = scoreNameTextSize
        paint.color = ContextCompat.getColor(context, R.color.text_white)
        val highScoreRectF = RectF(highScoreLeft, nameStringY, highScoreRight, nameStringY + highScoreHeight / 3)
        val highScoreNameXY = AramisViewHelper.getDrawTextXY(highScoreT, highScoreRectF, paint)
        canvas.drawText(highScoreT, highScoreNameXY[0], highScoreNameXY[1], paint)

        //score
        val scoreT = "score"
        scoreRight = highScoreLeft - gridMargin
        scoreLeft = scoreRight - cellSize
        paint.textSize = scoreNameTextSize
        scoreDrawable?.setBounds(scoreLeft.toInt(), nameStringY.toInt(), scoreRight.toInt(), highScoreBottom.toInt())
        scoreDrawable?.draw(canvas)
        highScoreRectF.set(scoreLeft, nameStringY, scoreRight, nameStringY + highScoreHeight / 3)
        val scoreNameXY = AramisViewHelper.getDrawTextXY(scoreT, highScoreRectF, paint)
        canvas.drawText(scoreT, scoreNameXY[0], scoreNameXY[1], paint)
        scoreTop = nameStringY + highScoreHeight / 4
        scoreBottom = highScoreBottom


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

    private val titleRectF = RectF()

    private fun initDimensions() {
        cellSize = min(height / (M2048.numX + 2f), width / (M2048.numX + 1f))
        cellDivider = cellSize / 7
        gridMargin = (width - cellSize * M2048.numX - cellDivider * (M2048.numX + 1)) / 2f

        startingX = gridMargin
        startingY = height / 2f - cellSize * 3 / 2
        endingX = startingX + cellSize * M2048.numX + cellDivider * (M2048.numX + 1)
        endingY = startingY + cellSize * M2048.numY + cellDivider * (M2048.numX + 1)

        workingWidth = (endingX - startingX)
        workingHeight = (endingY - startingY)
        val overlayButtonWidth = cellSize * 3 / 2
        val overlayButtonHeight = cellSize / 2
        val overlayFirstButtonLeft = workingWidth / 2 - overlayButtonWidth / 2
        val overlayFirstButtonTop = workingHeight / 2 - overlayButtonHeight / 2 + dip(30)
        val overlayFirstButtonRight = overlayFirstButtonLeft + overlayButtonWidth
        val overlayFirstButtonBottom = overlayFirstButtonTop + overlayButtonHeight

        val overlaySecondButtonTop = overlayFirstButtonTop + dip(30)
        val overlaySecondButtonBottom = overlayFirstButtonBottom + dip(30)

        firstButtonRectF.set(overlayFirstButtonLeft, overlayFirstButtonTop, overlayFirstButtonRight, overlayFirstButtonBottom)
        secondButtonRectF.set(overlayFirstButtonLeft, overlaySecondButtonTop, overlayFirstButtonRight, overlaySecondButtonBottom)

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        game.release()
    }


}