package com.aramis.aramisapp.game.g2048

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.aramis.aramisapp.R
import com.aramis.library.extentions.logE
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 *Created by Aramis
 *Date:2018/10/11
 *Description:
 */
class G2048View : View {

    private val paint = Paint()
    private var cellSize = 0
    lateinit var game: Game2048
    //文字大小
    private var textSize = 0f
    private var cellTextSize = 0f
    private var headerTextSize = 0f
    //内容绘制区域
    private var startingX = 0f
    private var endingX = 0f
    private var startingY = 0f
    private var endingY = 0f
    //
    private var gridWidth = 0
    //Icon variables
    var sYIcons = 0
    var sXNewGame = 0
    var sXUndo = 0
    var iconSize = 0
        private set
    private var sYAll = 0f
    //Timing
    private var lastFPSTime=System.nanoTime()
    //Assets
    private var backgroundRectangle: Drawable? = null

    //单元格
    val numCellTypes = 21
    private val bitmapCell = mutableListOf<BitmapDrawable>()

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private var background: Bitmap? = null

    private fun init() {
        game = Game2048(context, this)

        //画笔设置字体
        paint.typeface = Typeface.createFromAsset(resources.assets, "ClearSans-Bold.ttf")
        //画笔无锯齿
        paint.isAntiAlias = true
        backgroundRectangle = ContextCompat.getDrawable(context, R.drawable.background_rectangle)


        setOnTouchListener(InputListener(this))
        //开始新游戏
        game.newGame()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        logE("onSizeChanged")
        getLayout(w, h)
        createBitmapCells()
        createBackgroundBitmap(w, h)
        createOverlays()
    }

    //创建"继续游戏"，"游戏失败"等覆盖层
    private fun createOverlays() {

    }

    private fun createBitmapCells() {
        val cellRectangleIds = getCellRectangleIds()
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = cellTextSize
        for (i in 0 until numCellTypes) {
            val value = 2.0.pow(i)
            val tempTextSize = cellTextSize * cellSize * 0.9f / max(cellSize * 0.9f, paint.measureText(value.toString()))
            paint.textSize = tempTextSize
            val bitmap = Bitmap.createBitmap(cellSize, cellSize, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawDrawable(canvas, ContextCompat.getDrawable(context, cellRectangleIds[i])!!, 0, 0, cellSize, cellSize)
            drawCellText(canvas, value.toInt())
            bitmapCell.add(BitmapDrawable(resources, bitmap))
        }
        logE("createBitmapCells:${bitmapCell.size}")

    }

    //绘制drawable
    private fun drawDrawable(canvas: Canvas, drawable: Drawable?, startingX: Int, startingY: Int, endingX: Int, endingY: Int) {
        drawable?.setBounds(startingX, startingY, endingX, endingY)
        drawable?.draw(canvas)
    }

    //绘制cell文字
    private fun drawCellText(canvas: Canvas, value: Int) {
        val textShiftY = centerText()
        val colorId = if (value >= 8) R.color.text_white else R.color.text_black
        paint.color = ContextCompat.getColor(context, colorId)
        canvas.drawText(value.toString(), cellSize / 2f, cellSize / 2f - textShiftY, paint)
    }

    //获取单元格的id列表
    private fun getCellRectangleIds(): List<Int> {
        val cellRectangleIds = mutableListOf(R.drawable.cell_rectangle,
                R.drawable.cell_rectangle_2,
                R.drawable.cell_rectangle_4,
                R.drawable.cell_rectangle_8,
                R.drawable.cell_rectangle_16,
                R.drawable.cell_rectangle_32,
                R.drawable.cell_rectangle_64,
                R.drawable.cell_rectangle_128,
                R.drawable.cell_rectangle_256,
                R.drawable.cell_rectangle_512,
                R.drawable.cell_rectangle_1024,
                R.drawable.cell_rectangle_2048)

        (12 until numCellTypes).forEach { cellRectangleIds.add(R.drawable.cell_rectangle_4096) }
        logE(cellRectangleIds.toString())
        return cellRectangleIds
    }

    //创建背景
    private fun createBackgroundBitmap(width: Int, height: Int) {
        background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        logE("createBackgroundBitmap width:$width,height:$height")
        background?.apply {
            val canvas = Canvas(this)
            drawHeader(canvas)
            drawBackground(canvas)
            drawBackgroundGrid(canvas)
        }
    }

    private fun drawBackgroundGrid(canvas: Canvas) {
        val backgroundCell = ContextCompat.getDrawable(context, R.drawable.cell_rectangle)
        (0 until game.numSquaresX).forEach { xx ->
            (0 until game.numSquaresY).forEach { yy ->
                val sX = startingX + gridWidth + (cellSize + gridWidth) * xx
                val eX = sX + cellSize
                val sY = startingY + gridWidth + (cellSize + gridWidth) * yy
                val eY = sY + cellSize
                drawDrawable(canvas, backgroundCell, sX.toInt(), sY.toInt(), eX.toInt(), eY.toInt())
            }
        }
    }

    //绘制背景drawable
    private fun drawBackground(canvas: Canvas) {
        drawDrawable(canvas, backgroundRectangle, startingX.toInt(), startingY.toInt(), endingX.toInt(), endingY.toInt())
    }

    //绘制标题-->2048
    private fun drawHeader(canvas: Canvas) {
        paint.textSize = headerTextSize
        paint.color = ContextCompat.getColor(context, R.color.text_black)
        paint.textAlign = Paint.Align.LEFT
        val textShiftY = centerText() * 2
        val headerStartY = sYAll - textShiftY
        canvas.drawText(resources.getString(R.string.header), startingX, headerStartY, paint)
    }

    //初始化属性参数
    private fun getLayout(width: Int, height: Int) {
        cellSize = min(width / (game.numSquaresX + 1), height / (game.numSquaresY + 3))
        gridWidth = cellSize / 7
        val screenMiddleX = width / 2
        val screenMiddleY = height / 2
        val boardMiddleY = screenMiddleY + cellSize / 2
        iconSize = cellSize / 2

        //Grid Dimensions
        val halfNumSquaresX = game.numSquaresX / 2.0f
        val halfNumSquaresY = game.numSquaresY / 2.0f
        startingX = screenMiddleX - (cellSize + gridWidth) * halfNumSquaresX - gridWidth / 2
        endingX = screenMiddleX + (cellSize + gridWidth) * halfNumSquaresX + gridWidth / 2
        startingY = boardMiddleY - (cellSize + gridWidth) * halfNumSquaresY - gridWidth / 2
        endingY = boardMiddleY + (cellSize + gridWidth) * halfNumSquaresY + gridWidth / 2

        //Text Dimensions
        paint.textSize = cellSize.toFloat()
        textSize = cellSize * cellSize / max(cellSize.toFloat(), paint.measureText("0000"))
        headerTextSize = textSize * 2
        cellTextSize = textSize

        //static variables
        sYAll = startingY - cellSize * 1.5f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        logE("onDraw")
        background?.apply {
            canvas?.drawBitmap(this, 0f, 0f, paint)
        }

        drawCells(canvas)

        drawTest(canvas)
    }

    private fun drawCells(canvas: Canvas?) {
        logE("drawCells drawCells")
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER
        for (xx in 0 until game.numSquaresX) {
            for (yy in 0 until game.numSquaresY) {
                val sX = (startingX + gridWidth + (cellSize + gridWidth) * xx).toInt()
                val eX = sX + cellSize
                val sY = (startingY + gridWidth + (cellSize + gridWidth) * yy).toInt()
                val eY = sY + cellSize

                val currentTile = game.grid.getCellContent(xx, yy)
                if (currentTile != null && canvas != null) {
                    val value = currentTile.value
                    val index = sqrt(value.toFloat()).toInt()
                    //动画列表
                    val aArray = game.aGrid.getAnimationCell(xx, yy)
                    logE("aArray.size:${aArray.size}")
                    if (aArray.isNotEmpty()) {
                        aArray.forEach { aCell ->
                            val percentageDone = aCell.getPercentageDone().toFloat()
                            when (aCell.animationType) {
                                Game2048.SPAWN_ANIMATION -> { //进入动画
                                    paint.textSize = percentageDone
                                    val cellScaleSize = cellSize / 2 * (1 - percentageDone)
                                    bitmapCell[index].setBounds((sX + cellScaleSize).toInt(), (sY + cellScaleSize).toInt(), (eX - cellScaleSize).toInt(), (eY - cellScaleSize).toInt())
                                    bitmapCell[index].draw(canvas)
                                }
                                Game2048.MOVE_ANIMATION -> {
                                    var tempIndex = index
                                    if (aArray.size >= 2) {
                                        tempIndex -= 1
                                    }
                                    val previousX = aCell.extras?.get(0) ?: -1
                                    val previousY = aCell.extras?.get(1) ?: -1
                                    logE("previousX:$previousX,previousY:$previousY")
                                    if (previousX != -1 && previousY != -1) {
                                        val currentX = currentTile.x
                                        val currentY = currentTile.y
                                        val dX = ((currentX - previousX) * (cellSize + gridWidth) * (percentageDone - 1) * 1.0).toInt()
                                        val dY = ((currentY - previousY) * (cellSize + gridWidth) * (percentageDone - 1) * 1.0).toInt()
                                        bitmapCell[tempIndex].setBounds(sX + dX, sY + dY, eX + dX, eY + dY)
                                        bitmapCell[tempIndex].draw(canvas)
                                    }
                                }
                            }
                        }
                    } else {
                        logE("xx:$xx,yy:$yy,currentTile:$currentTile,index:$index")
                        bitmapCell[index].setBounds(sX, sY, eX, eY)
                        bitmapCell[index].draw(canvas)
                    }
                }
            }
        }

    }

    private fun resyncTime(){

    }

    private fun log2(n: Int): Int {
        if (n <= 0) throw IllegalArgumentException()
        return 31 - Integer.numberOfLeadingZeros(n)
    }

    //测试
    private fun drawTest(canvas: Canvas?) {
        paint.color = ContextCompat.getColor(context, R.color.text_black)
        var a = 0f
        while (cellSize * a < width) {
            canvas?.drawLine(cellSize * a, 0f, cellSize * a, height.toFloat(), paint)
            a++
        }
        a = 0f
        while (cellSize * a < height) {
            canvas?.drawLine(0f, cellSize * a, width.toFloat(), cellSize * a, paint)
            a++
        }

        paint.color = 0xffff5599.toInt()
        canvas?.drawLine(startingX, 0f, startingX, height.toFloat(), paint)
        canvas?.drawLine(endingX, 0f, endingX, height.toFloat(), paint)
        canvas?.drawLine(0f, startingY, width.toFloat(), startingY, paint)
        canvas?.drawLine(0f, endingY, width.toFloat(), endingY, paint)

//        canvas?.drawBitmap(bitmapCell[0],50,50,paint)
//        bitmapCell[0].draw(canvas!!)
//        bitmapCell[1].draw(canvas)


    }

    //移除时，回收资源
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        background?.recycle()
    }

    //工具--文字居中
    private fun centerText(): Float {
        return (paint.descent() + paint.ascent()) / 2
    }

    companion object {
        const val BASE_ANIMATION_TIME = 100000000
    }

}