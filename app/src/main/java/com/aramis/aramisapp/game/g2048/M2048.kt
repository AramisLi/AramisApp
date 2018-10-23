package com.aramis.aramisapp.game.g2048

import android.content.Context
import android.content.SharedPreferences
import com.aramis.library.extentions.logE
import org.jetbrains.anko.toast
import kotlin.math.abs

/**
 *Created by Aramis
 *Date:2018/10/16
 *Description:
 */
class M2048(val view: M2048View) {

    private val cacheMatrix = Array(numX) { xx ->
        Array(numY) { yy ->
            MCell(xx, yy, 0)
        }
    }

    val handleMatrix = Array(numX) { xx ->
        Array(numY) { yy ->
            MCell(xx, yy, 0)
        }
    }

    private val forwardMatrix = Array(numX) { xx ->
        Array(numY) { yy ->
            MCell(xx, yy, 0)
        }
    }

    var onGameOverListener: (() -> Unit)? = null
    var onGameSuccessListener: (() -> Unit)? = null
    var onGameFinalWinListener: (() -> Unit)? = null

    private val context = view.context

    val mergedCells = mutableListOf<MCell>()
    val mergedCellPairs = mutableListOf<Pair<Int, Int>>()
    //单元格个数
    private var cellsCount = 0

    private val testNumberArray = arrayOf(0, 2, 4, 8, 16, 32,
            64, 128, 256, 512, 1024, 2048, 4096, 8192,
            16384, 32768, 65536, 131072)
    //分数
    var score: Int = 0
        private set
    //最高分数
    var highScore: Int = 0
        private set
    private var tempScore = 0
    private var tempHighScore = 0
    var randomAddCell: MCell? = null
        private set

    var finalMode = false

    init {
        score = getSP().getInt(KEY_SCORE, 0)
        highScore = getSP().getInt(KEY_HIGH_SCORE, 0)
        finalMode = getSP().getBoolean(KEY_FINAL_MODE, false)
    }


    fun startGame() {
        initMatrix()
    }

    fun release() {
        if (view.state == M2048View.M2048ViewState.Running) {
            saveMatrix(handleMatrix)
            saveScore()
        }
    }

    fun reset() {
        score = 0
        saveScore()
        spMatrixClear()
        forEachMatrix { xx, yy ->
            handleMatrix[xx][yy].value = 0
            handleMatrix[xx][yy].clear()
        }
        initMatrix()
    }

    fun move(direction: Int) {

//        when (direction) {
//            MOVE_LEFT -> {
//                logE("左移")
//            }
//            MOVE_UP -> {
//                logE("上移")
//            }
//            MOVE_RIGHT -> {
//                logE("右移")
//            }
//            MOVE_DOWN -> {
//                logE("下移")
//            }
//        }
//        logE("origin:")
//        printHandleMatrix2()
        val moved = formatHandleMatrix(direction)
//        logE("格式化数组:")
//        printHandleMatrix2()

        if (moved) {
            formatForwardMatrix(direction)
            view.startAnimation(direction)
        }
    }

    private fun formatForwardMatrix(direction: Int) {
        mergedCells.clear()
        val horizontal = direction == M2048.MOVE_LEFT || direction == M2048.MOVE_RIGHT
        val sign = if (direction == M2048.MOVE_RIGHT || direction == M2048.MOVE_DOWN) 1 else -1
//        forEachMatrix(forwardMatrix) { xx, yy ->
//            forwardMatrix[xx][yy].value = 0
//        }
        //计数归零
        cellsCount = 0
        forEachMatrix { xx, yy ->
            //缓存
//            cacheMatrix[xx][yy].value = handleMatrix[xx][yy].value

            val cell = if (horizontal) {
                val isLeft = direction == M2048.MOVE_LEFT
                val tempY = if (isLeft) yy else handleMatrix[xx].size - yy - 1
                handleMatrix[xx][tempY]
            } else {
                val isUp = direction == M2048.MOVE_UP
                val tempX = if (isUp) xx else handleMatrix[xx].size - xx - 1
                handleMatrix[tempX][yy]
            }
            if (cell.movedX > 0 || cell.movedY > 0) {
                val mx = if (horizontal) cell.x else cell.x + sign * cell.movedX
                val my = if (horizontal) cell.y + sign * cell.movedY else cell.y
                forwardMatrix[mx][my].value += cell.value
                forwardMatrix[cell.x][cell.y].value = 0

                //添加到合并cell数组
                addMergedCell(forwardMatrix[mx][my], cell.mergeCell != null)
            } else if (cell.value > 0) {
                forwardMatrix[cell.x][cell.y].value = cell.value
            } else {
                forwardMatrix[cell.x][cell.y].value = 0
            }

            if (forwardMatrix[cell.x][cell.y].value > 0) {
                cellsCount++
            }
        }

        randomAddCell = getRandomCell2()
    }


    private fun formatHandleMatrix(direction: Int): Boolean {
        var moved = false
        val horizontal = direction == M2048.MOVE_LEFT || direction == M2048.MOVE_RIGHT

        fun followedEachAdd(xx: Int, yy: Int, isLeftOrUp: Boolean) {
            if (horizontal) {
                val range = if (isLeftOrUp) yy + 1 until handleMatrix[yy].size else yy - 1 downTo 0
                range.filter { handleMatrix[xx][it].value != 0 }
                        .forEach {
                            val other = handleMatrix[xx][it]
                            if (other.mergeCell == null) {
                                moved = true
                                other.movedY += 1
                            }
                        }
            } else {
                val range = if (isLeftOrUp) xx + 1 until handleMatrix[xx].size else xx - 1 downTo 0
                range.filter { handleMatrix[it][yy].value != 0 }
                        .forEach {
                            val other = handleMatrix[it][yy]
                            if (other.mergeCell == null) {
                                moved = true
                                other.movedX += 1
                            }
                        }
            }

        }

        forEachMatrix { xx, yy ->

            if (horizontal) {
                val isLeft = direction == M2048.MOVE_LEFT
                val currentCellY = if (isLeft) yy else handleMatrix.size - yy - 1
                val cell = handleMatrix[xx][if (isLeft) yy else handleMatrix.size - yy - 1]

                if (cell.value == 0) {
                    followedEachAdd(xx, currentCellY, isLeft)
                } else {
                    //前一个要检查的单元格的Y
                    var nextCellY = if (isLeft) currentCellY - 1 else currentCellY + 1
                    //在可执行的范围内。既向左移动时不为0，向右移动时小于3（handleMatrix[xx].size - 1）
                    while (if (isLeft) nextCellY >= 0 else nextCellY <= handleMatrix[xx].size - 1) {
                        //检查 被检查的单元格和当前cell间的单元格的值是不是0，是合并，不是不合并
                        var checkBetweenIsZero = true
                        if (abs(nextCellY - currentCellY) > 1) {
                            val start = if (isLeft) nextCellY + 1 else currentCellY
                            val end = if (isLeft) currentCellY else nextCellY - 1
//                            println("start:$start,end:$end")
                            for (i in (start until end)) {
                                if (handleMatrix[xx][i].value != 0) {
                                    checkBetweenIsZero = false
                                    break
                                }
                            }
                        }

                        if (checkBetweenIsZero && handleMatrix[xx][nextCellY].value == cell.value && handleMatrix[xx][nextCellY].mergeCell == null && cell.mergeCell == null) {
                            moved = true
                            addScore(cell.value * 2)
                            cell.movedY += 1
                            cell.mergeCell = handleMatrix[xx][nextCellY]
                            handleMatrix[xx][nextCellY].mergeCell = cell
                            handleMatrix[xx][nextCellY].mergeFirst = true
                            followedEachAdd(xx, currentCellY, isLeft)
//                            println("当前单元格:(xx:$xx,yy:$currentCellY,value:${handleMatrix[xx][currentCellY].value}mergeFirst:${handleMatrix[xx][currentCellY].mergeFirst}),要合并的单元格:(xx:$xx,yy:$nextCellY,value:${handleMatrix[xx][nextCellY].value},mergeFirst:${handleMatrix[xx][nextCellY].mergeFirst})")
                        }
                        //下一个单元格
                        nextCellY = if (isLeft) nextCellY - 1 else nextCellY + 1

                    }
                }
            } else {
                val isUp = direction == M2048.MOVE_UP
                val currentCellX = if (isUp) xx else handleMatrix.size - xx - 1
                val cell = handleMatrix[currentCellX][yy]

                if (cell.value == 0) {
                    followedEachAdd(currentCellX, yy, isUp)
                } else {
//                    println("当前单元格:(xx:$currentCellX,yy:$yy,value:${handleMatrix[currentCellX][yy].value}mergeFirst:${handleMatrix[currentCellX][yy].mergeFirst})")
                    var nextCellX = if (isUp) currentCellX - 1 else currentCellX + 1
                    while (if (isUp) nextCellX >= 0 else nextCellX <= handleMatrix[xx].size - 1) {
//                        println("nextCellX:$nextCellX,currentCellX:$currentCellX")
                        //检查 被检查的单元格和当前cell间的单元格的值是不是0，是合并，不是不合并
                        var checkBetweenIsZero = true
                        if (abs(nextCellX - currentCellX) > 1) {
                            val start = if (isUp) nextCellX + 1 else currentCellX
                            val end = if (isUp) currentCellX else nextCellX - 1
                            for (i in (start until end)) {
                                if (handleMatrix[i][yy].value != 0) {
                                    checkBetweenIsZero = false
                                    break
                                }
                            }
                        }

                        if (checkBetweenIsZero && handleMatrix[nextCellX][yy].value == cell.value && handleMatrix[nextCellX][yy].mergeCell == null && cell.mergeCell == null) {
                            moved = true
                            addScore(cell.value * 2)
                            cell.movedX += 1
                            cell.mergeCell = handleMatrix[nextCellX][yy]
                            handleMatrix[nextCellX][yy].mergeCell = cell
                            handleMatrix[nextCellX][yy].mergeFirst = true
                            followedEachAdd(currentCellX, yy, isUp)
//                            println("当前单元格:(xx:$currentCellX,yy:$yy,value:${handleMatrix[currentCellX][yy].value},mergeFirst:${handleMatrix[currentCellX][yy].mergeFirst}),要合并的单元格:(xx:$nextCellX,yy:$yy,value:${handleMatrix[nextCellX][yy].value},mergeFirst:${handleMatrix[nextCellX][yy].mergeFirst})")
                        }
                        nextCellX += if (isUp) -1 else 1
                    }
                }
            }
        }
        return moved
    }

    private fun addScore(s: Int) {
        this.score += s
        if (this.highScore <= score) {
            highScore = score
        }
    }

    private fun printHandleMatrix2(matrix: Array<Array<MCell>> = handleMatrix) {
        val s = StringBuilder()
        matrix.forEachIndexed { index, ints ->
            ints.forEach { s.append("(x:${it.x},y:${it.y},value:${it.value},movedX:${it.movedX},movedY:${it.movedY}),") }
            s.append("\n")
        }

        logE(s.toString())
    }

    //刷新数组
    fun refreshHandleMatrix(direction: Int) {
        logE("刷新前：")
        printHandleMatrix2()

        forEachMatrix { xx, yy ->
            //缓存
            cacheMatrix[xx][yy].value = handleMatrix[xx][yy].value

            handleMatrix[xx][yy].value = forwardMatrix[xx][yy].value

            handleMatrix[xx][yy].clear()

            forwardMatrix[xx][yy].value = 0
        }

        if (isGameOver()) {
            if (finalMode && isFinalWin()) {
                onGameFinalWinListener?.invoke()
            } else {
                onGameOverListener?.invoke()
            }
        } else {
            randomAddCell?.apply {
                handleMatrix[this.x][this.y].value = this.value
                logE("添加 随机单元格${handleMatrix[this.x][this.y]}")
            }
            if (isGameOver()) {
                onGameOverListener?.invoke()
            }
            view.invalidate()
            testPrint()
        }
    }

    private fun testPrint() {
        if (debug) {
            var cellError = false
            var errorCell: MCell? = null
            for (matrix in handleMatrix) {
                for (mCell in matrix) {
                    if (mCell.value !in testNumberArray) {
                        cellError = true
                        errorCell = mCell
                        break
                    }
                }
            }
            if (cellError) {
                context.toast("错误$errorCell")
            }
            logE("刷新数组:")
            printHandleMatrix2()
        }
    }


    //添加到合并cell数组
    private fun addMergedCell(cell: MCell, isMerged: Boolean) {
        if (isMerged) {
            var isExist = false
            var index = 0
            for (mergedCell in mergedCells) {
                if (mergedCell.x == cell.x && mergedCell.y == cell.y) {
                    isExist = true
                    break
                }
                index++
            }

            if (isExist) {
                mergedCells[index].value = cell.value
            } else {
                mergedCells.add(MCell(cell.x, cell.y, cell.value))
            }
        }

        logE("mergedCells.size:${mergedCells.size}")

    }

    private fun forEachMatrix(callback: (xx: Int, yy: Int) -> Unit) {
        for (xx in 0 until numX) {
            for (yy in 0 until numY) {
                callback.invoke(xx, yy)
            }
        }
    }

    private fun initMatrix() {
        val lastMatrix = getSP().getString(KEY_LAST_MATRIX, null)
//        val lastMatrix="4096,2048,1024,2," +
//                "128,32,128,64," +
//                "64,256,16,256," +
//                "16,32,4,0"
//
//        val lastMatrix = "131072,65536,32768,16384," +
//                "8192,4096,2048,1024," +
//                "512,256,128,64," +
//                "32,16,8,4"
        if (lastMatrix != null && lastMatrix.isNotBlank()) {

            var row = 0
            lastMatrix.split(",").forEachIndexed { index, s ->
                handleMatrix[row][index % numX].value = s.toInt()
                logE("row:$row,index % numX:${index % numX}")
                if (index != 0 && index % numX == numX - 1) {
                    row++
                }
            }

        } else {
            //new game
            addRandomCell(2)
        }
        logE("lastMatrix:$lastMatrix")
        printHandleMatrix2()

        copyAllValue(handleMatrix, forwardMatrix)
    }

    private fun isFinalWin(): Boolean {
        val allValues = mutableListOf<Int>()
        forEachMatrix { xx, yy ->
            allValues.add(handleMatrix[xx][yy].value)
        }

        for (i in (testNumberArray.size - 1 downTo 3)) {
            if (testNumberArray[i] !in allValues) {
                return false
            }
        }
        return true
    }

    private fun isGameOver(): Boolean {

        for (xx in (0 until handleMatrix.size)) {
            for (yy in (0 until handleMatrix[0].size)) {
                val cell = handleMatrix[xx][yy]
                if (cell.value == 0) {
                    return false
                }

                if (xx < handleMatrix.size - 1 && cell.value == handleMatrix[xx + 1][yy].value) {
                    return false
                }

                if (yy < handleMatrix.size - 1 && cell.value == handleMatrix[xx][yy + 1].value) {
                    return false
                }

            }
        }


        return true
    }

    //添加随机cell
    private fun addRandomCell(n: Int = 1) {

        if (cellsCount < numX * numY / 2) {
            var t = 0
            while (t < n) {
                val x = (0 until numX).shuffled().first()
                val y = (0 until numY).shuffled().first()
                if (handleMatrix[x][y].value == 0) {
                    handleMatrix[x][y].value = getRandomValue()
                    handleMatrix[x][y].isNewBorn = true
                    t++
                }
            }
        } else {
            val emptyCells = mutableListOf<MCell>()
            forEachMatrix { xx, yy ->
                if (handleMatrix[xx][yy].value == 0) {
                    emptyCells.add(handleMatrix[xx][yy])
                }
            }
            if (emptyCells.isNotEmpty()) {
                val cell = emptyCells.shuffled().first()
                cell.value = getRandomValue()
                cell.isNewBorn = true
            }

        }
    }

    //添加随机cell
    private fun getRandomCell2(): MCell? {

        if (cellsCount < numX * numY / 2) {
            while (true) {
                val x = (0 until numX).shuffled().first()
                val y = (0 until numY).shuffled().first()
                if (forwardMatrix[x][y].value == 0) {
                    return MCell(x, y, getRandomValue(), isNewBorn = true)
                }
            }
        } else {
            val emptyCells = mutableListOf<MCell>()
            forEachMatrix { xx, yy ->
                if (forwardMatrix[xx][yy].value == 0) {
                    emptyCells.add(MCell(xx, yy, forwardMatrix[xx][yy].value, isNewBorn = true))
                }
            }
            if (emptyCells.isNotEmpty()) {
                val cell = emptyCells.shuffled().first()
                cell.value = getRandomValue()
                return cell
            }

        }
        return null
    }

    private fun getRandomValue(): Int {
        return if (Math.random() < 0.9) 2 else 4
    }

    private fun saveMatrix(matrix: Array<Array<MCell>>) {
        val s = StringBuilder()
        matrix.forEach { arr ->
            arr.forEach {
                s.append(it.value)
                s.append(",")
            }
        }
        s.deleteCharAt(s.length - 1)
        getSP().edit().putString(KEY_LAST_MATRIX, s.toString()).apply()
    }

    private fun spMatrixClear() {
        getSP().edit().putString(KEY_LAST_MATRIX, "").apply()
    }

    private fun saveScore() {
        val sp = getSP()
        sp.edit().putInt(KEY_SCORE, score).apply()
        val cHighScore = sp.getInt(KEY_HIGH_SCORE, 0)
        if (highScore > cHighScore) {
            sp.edit().putInt(KEY_HIGH_SCORE, highScore).apply()
        }
    }

    private fun getSP(): SharedPreferences =
            context.getSharedPreferences("my2048", Context.MODE_PRIVATE)

    private fun copyAllValue(from: Array<Array<MCell>>, to: Array<Array<MCell>>) {
        (0 until from.size).forEach { xx ->
            (0 until from[0].size).forEach { yy ->
                to[xx][yy].value = from[xx][yy].value
            }
        }
    }

    companion object {
        var debug = true
        const val numX = 4
        const val numY = 4

        private const val KEY_LAST_MATRIX = "lastMatrix"
        private const val KEY_SCORE = "score"
        private const val KEY_HIGH_SCORE = "highScore"
        private const val KEY_FINAL_MODE = "finalMode"

        const val MOVE_LEFT = 1
        const val MOVE_UP = 2
        const val MOVE_RIGHT = 3
        const val MOVE_DOWN = 4
    }
}

data class MCell(val x: Int, val y: Int, var value: Int, var mergeCell: MCell? = null, var mergeFirst: Boolean = false,
                 var movedX: Int = 0, var movedY: Int = 0, var isNewBorn: Boolean = false) {

    fun clear() {
        this.mergeCell = null
        this.mergeFirst = false
        this.movedX = 0
        this.movedY = 0
        this.isNewBorn = false
    }

    fun shortString(): String {
        return "(x=$x, y=$y, value=$value,movedX=$movedX,movedY:$movedY)"
    }

    override fun toString(): String {
        return "MCell(x=$x, y=$y, value=$value," +
                "mergeCell=(${if (mergeCell != null) "x:${mergeCell?.x},y:${mergeCell?.y},value:${mergeCell?.value}" else null})," +
                "mergeFirst:$mergeFirst,movedX=$movedX,movedY:$movedY)"
    }
}