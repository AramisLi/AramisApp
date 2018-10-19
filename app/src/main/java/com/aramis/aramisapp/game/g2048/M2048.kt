package com.aramis.aramisapp.game.g2048

import android.content.Context
import android.content.SharedPreferences
import com.aramis.library.extentions.logE

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

    private val cellNumMax = numX * numY
    private val context = view.context

    val mergedCells = mutableListOf<MCell>()

    fun startGame() {
        initMatrix()
    }

    fun move(direction: Int) {

        when (direction) {
            MOVE_LEFT -> {
                logE("左移")
            }
            MOVE_UP -> {
                logE("上移")
            }
            MOVE_RIGHT -> {
                logE("右移")
            }
            MOVE_DOWN -> {
                logE("下移")
            }
        }

        formatHandleMatrix(direction)
        printHandleMatrix2()
        view.startAnimation(direction)
    }


    private fun formatHandleMatrix(direction: Int) {
        val horizontal = direction == M2048.MOVE_LEFT || direction == M2048.MOVE_RIGHT

        fun followedEachAdd(xx: Int, yy: Int, isLeftOrUp: Boolean) {
            if (horizontal) {
                val range = if (isLeftOrUp) yy + 1 until handleMatrix[yy].size else yy - 1 downTo 0
                range.filter { handleMatrix[xx][it].value != 0 }
                        .forEach {
                            val other = handleMatrix[xx][it]
                            other.movedY += 1
                        }
            } else {
                val range = if (isLeftOrUp) xx + 1 until handleMatrix[xx].size else xx - 1 downTo 0
                range.filter { handleMatrix[it][yy].value != 0 }
                        .forEach {
                            val other = handleMatrix[it][yy]
                            other.movedX += 1
                        }
            }

        }

        forEachMatrix(handleMatrix) { xx, yy ->

            if (horizontal) {
                val isLeft = direction == M2048.MOVE_LEFT
                var tempY = if (isLeft) yy else handleMatrix.size - yy - 1
                val cell = handleMatrix[xx][tempY]
                if (cell.value == 0) {
                    followedEachAdd(xx, tempY, isLeft)
                } else {
                    val b = if (isLeft) tempY > 0 else tempY < handleMatrix[xx].size - 1
                    while (b && (if (isLeft) tempY > 0 else tempY < handleMatrix[xx].size - 1)) {
                        val position = if (isLeft) tempY - 1 else tempY + 1
                        if (handleMatrix[xx][position].value == cell.value && handleMatrix[xx][position].mergeCell == null && cell.mergeCell == null) {
                            cell.movedY += 1
                            cell.mergeCell = handleMatrix[xx][position]
                            handleMatrix[xx][position].mergeCell = cell
                            handleMatrix[xx][position].mergeFirst = true
                            followedEachAdd(xx, tempY, isLeft)
                        }
                        tempY += if (isLeft) -1 else 1
                    }
                }
            } else {
                val isUp = direction == M2048.MOVE_UP
                var tempX = if (isUp) xx else handleMatrix.size - xx - 1
                val cell = handleMatrix[tempX][yy]
                if (cell.value == 0) {
                    followedEachAdd(tempX, yy, isUp)
                } else {
                    val b = if (isUp) tempX > 0 else tempX < handleMatrix[xx].size - 1
                    while (b && (if (isUp) tempX > 0 else tempX < handleMatrix[xx].size - 1)) {
                        val position = if (isUp) tempX - 1 else tempX + 1
                        if (handleMatrix[position][yy].value == cell.value && handleMatrix[position][yy].mergeCell == null && cell.mergeCell == null) {
                            cell.movedX += 1
                            cell.mergeCell = handleMatrix[position][yy]
                            handleMatrix[position][yy].mergeCell = cell
                            handleMatrix[position][yy].mergeFirst = true
                            followedEachAdd(tempX, yy, isUp)
                        }
                        tempX += if (isUp) -1 else 1
                    }
                }
            }
        }
    }

    private fun printHandleMatrix2() {
        val s=StringBuilder()
        handleMatrix.forEachIndexed { index, ints ->
            ints.forEach { s.append("(x:${it.x},y:${it.y},value:${it.value},movedX:${it.movedX},movedY:${it.movedY}),") }
            s.append("\n")
        }

        logE(s.toString())
    }


    fun refreshHandleMatrix(direction: Int) {
        mergedCells.clear()
        val horizontal = direction == M2048.MOVE_LEFT || direction == M2048.MOVE_RIGHT
        val sign = if (direction == M2048.MOVE_RIGHT || direction == M2048.MOVE_DOWN) 1 else -1

        val tempMatrix = Array(M2048.numX) { xx ->
            Array(M2048.numY) { yy ->
                MCell(xx, yy, 0)
            }
        }
        printHandleMatrix2()
        forEachHandleMatrix { xx, yy ->
            val cell = handleMatrix[xx][yy]
            //缓存
            cacheMatrix[xx][yy].value = cell.value

            if (cell.movedX > 0 || cell.movedY > 0) {
                val mx = if (horizontal) cell.x else cell.x + sign * cell.movedX
                val my = if (horizontal) cell.y + sign * cell.movedY else cell.y
                logE("cell.x:${cell.x},cell.y:${cell.y},movedX:${cell.movedX},movedY:${cell.movedY},mx:$mx,my:$my")
                handleMatrix[mx][my].value = if (handleMatrix[mx][my].mergeFirst) cell.value * 2 else cell.value

                tempMatrix[mx][my].value = handleMatrix[mx][my].value
                mergedCells.add(tempMatrix[mx][my])
            } else if (cell.value != 0&& cell.mergeCell == null) {
                tempMatrix[xx][yy].value = cell.value
            }
        }

        forEachHandleMatrix { xx, yy ->
            handleMatrix[xx][yy].value = tempMatrix[xx][yy].value
            handleMatrix[xx][yy].clear()
        }

        addRandomCell()
    }

    private fun forEachHandleMatrix(callback: (xx: Int, yy: Int) -> Unit) {
        forEachMatrix(handleMatrix, callback)
    }

    private fun forEachMatrix(matrix: Array<Array<MCell>>, callback: (xx: Int, yy: Int) -> Unit) {
        for (xx in 0 until matrix.size) {
            for (yy in 0 until matrix[0].size) {
                callback.invoke(xx, yy)
            }
        }
    }

    private fun initMatrix() {
        val lastMatrix = getSP().getString(KEY_LAST_MATRIX, null)
        if (lastMatrix != null && lastMatrix.isNotBlank()) {
            var row = 0
            lastMatrix.split(",").forEachIndexed { index, s ->
                handleMatrix[row][index % numX].value = s.toInt()
                if (index != 0 && index % numX == 0) {
                    row++
                }
            }
        } else {
            //new game
            addRandomCell(2)
        }

        copyAll(forwardMatrix, handleMatrix)
    }

    private fun addRandomCell(n: Int = 1) {
        handleMatrix[0][2].value=2
        handleMatrix[1][3].value=2
        handleMatrix[0][0].value=2
        handleMatrix[1][0].value=2

//        var t = 0
//        while (t < n) {
//            val x = (0 until numX).shuffled().first()
//            val y = (0 until numY).shuffled().first()
//            if (handleMatrix[x][y].value == 0) {
//                handleMatrix[x][y].value = if (Math.random() < 0.9) 2 else 4
//                t++
//            }
//        }
    }

    private fun saveMatrix(matrix: Array<Array<Int>>) {
        val s = StringBuilder()
        matrix.forEach { arr ->
            arr.forEach {
                s.append(it)
                s.append(",")
            }
        }
        s.deleteCharAt(s.length - 1)
        getSP().edit().putString(KEY_LAST_MATRIX, s.toString()).apply()
    }

    private fun getSP(): SharedPreferences =
            context.getSharedPreferences("my2048", Context.MODE_PRIVATE)

    private fun copyAll(origin: Array<Array<MCell>>, copy: Array<Array<MCell>>) {
        (0 until origin.size).forEach { xx ->
            (0 until origin[0].size).forEach { yy ->
                origin[xx][yy].value = copy[xx][yy].value
            }
        }
    }

    companion object {
        const val numX = 4
        const val numY = 4

        private const val KEY_LAST_MATRIX = "lastMatrix"
        private const val KEY_SCORE = "score"
        private const val KEY_HIGH_SCORE = "highScore"

        const val MOVE_LEFT = 1
        const val MOVE_UP = 2
        const val MOVE_RIGHT = 3
        const val MOVE_DOWN = 4
    }
}

data class MCell(val x: Int, val y: Int, var value: Int, var mergeCell: MCell? = null, var mergeFirst: Boolean = false,
                 var movedX: Int = 0, var movedY: Int = 0) {

    fun clear() {
        this.mergeCell = null
        this.mergeFirst = false
        this.movedX = 0
        this.movedY = 0
    }

    fun shortString():String{
        return "(x=$x, y=$y, value=$value,movedX=$movedX,movedY:$movedY)"
    }

    override fun toString(): String {
        return "MCell(x=$x, y=$y, value=$value," +
                "mergeCell=(${if (mergeCell != null) "x:${mergeCell?.x},y:${mergeCell?.y},value:${mergeCell?.value}" else null})," +
                "mergeFirst:$mergeFirst,movedX=$movedX,movedY:$movedY)"
    }
}