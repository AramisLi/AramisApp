package com.aramis.aramisapp

import com.aramis.aramisapp.game.g2048.*
import com.aramis.aramisapp.game.paohuzi.Game
import com.aramis.aramisapp.game.paohuzi.bean.Pai
import com.aramis.aramisapp.game.sudoku.SudokuUtil
import com.aramis.aramisapp.pendulum.ColorHelper
import com.aramis.library.extentions.toHex
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun sanjiao() {
        val a = 3
        val b = 4
        val c = 5

        val alpha = sin(Math.toRadians(60.0))

        asin(0.5)

        println(alpha)
        println(Math.toDegrees(asin(0.5)))
        println(Math.toDegrees(sinh(0.5)))
    }

    @Test
    fun aa() {
        val test = listOf(
                listOf(0, 0, 7, 0, 0, 4, 0, 0, 8),
                listOf(0, 0, 0, 1, 0, 0, 4, 9, 0),
                listOf(0, 0, 0, 0, 6, 7, 3, 0, 0),
                listOf(0, 0, 2, 0, 0, 3, 0, 0, 1),
                listOf(0, 4, 0, 0, 0, 0, 0, 5, 0),
                listOf(7, 0, 0, 8, 0, 0, 6, 0, 0),
                listOf(0, 0, 4, 5, 3, 0, 0, 0, 0),
                listOf(0, 5, 1, 0, 0, 6, 0, 0, 0),
                listOf(0, 0, 0, 9, 0, 0, 5, 0, 0))

        val su = SudokuUtil()
//        val r = su.solve(test)
        val begin = System.currentTimeMillis()
        val r = su.checkMultiSolve(test)
        println("耗时:${System.currentTimeMillis() - begin}豪秒")
        r?.apply {
            println("有${r.size}个解")
            for (i in r) {
                println(i)
            }

            val size = r.size * r[0].size * r[0][0].size

            for (i in 0 until r.size) {

            }
        }

    }

    @Test
    fun bb() {
        val sudokuUtil = SudokuUtil()

        val begin = System.currentTimeMillis()
//        val randomSudoku = sudokuUtil.getRandomSudoku()

        val randomSudokuQuestion = sudokuUtil.getRandomSudokuQuestion(17)
        println("随机数独答案:")
        for (i in randomSudokuQuestion.first) {
            println(i)
        }
        println("随机数独问题:")
        for (i in randomSudokuQuestion.second) {
            println(i)
        }
        var seeCount = 0
        for (i in 0..8) {
            for (j in 0..8) {
                if (randomSudokuQuestion.second[i][j] > 0) {
                    seeCount++
                }
            }
        }
        println("随机数独问题共有${seeCount}个可见数")

        val solutions = sudokuUtil.checkMultiSolve(randomSudokuQuestion.second)
        println("此问题有${solutions?.size}个解")
        solutions?.apply {
            for (i in this) {
                println(i)
//                for (j in i){
//                    println(j)
//                }
//                println()
            }
        }
        println("耗时:${System.currentTimeMillis() - begin}毫秒")

    }

    @Test
    fun cc() {
//        val a = listOf(1, 2, 3, 4, 5)
//        val b = listOf(1, 2, 3, 4, 5)
//
//        println(a == b)
        val a = listOf(-1, 2, -3, 4, -5)
        val b = a.filter { it > 0 }
        println(b)
    }

    @Test
    fun dd() {
//        val array1 = arrayOf(arrayOf(
//                Tile(0, 1, 2)
//        ))

        val list = mutableListOf<MutableList<Tile?>>()

        (0 until 3).forEach { xx ->
            list.add((0 until 4).map { yy ->
                Tile(xx, yy, xx + yy)
            }.toMutableList())
        }
        println(list)

        (0 until list.size).forEach { xx ->
            (0 until list[0].size).forEach { yy ->
                list[xx][yy] = null
            }
        }
        println(list)

        val array1 = list.toTypedArray()
    }

    @Test
    fun testPaohuzi() {
        val game = Game(3)

        val l = game.getPaiList().toMutableList()
//        println(l.size)

        //随机取20张
        val play1PaiList = (0 until 20).map {
            val random = (0 until l.size).shuffled().first()
            l.removeAt(random)
        }.toMutableList()

        game.sortPais(play1PaiList)

//        play1PaiList.sortBy { it.value }
        printPaiList(play1PaiList)
//        println(l.size)
    }

    private fun printPaiList(paiList: List<Pai>) {
        paiList.forEach {
            println(it)
        }
    }

    private fun log2(n: Int): Int {
        return if (n <= 0) {
            -1
        } else {
            31 - Integer.numberOfLeadingZeros(n)
        }
    }

    private val handleMatrix = Array(M2048.numX) { xx ->
        Array(M2048.numY) { yy ->
            MCell(xx, yy, 0)
        }
    }

    @Test
    fun ee() {
        val forwardMatrix = Array(M2048.numX) { xx ->
            Array(M2048.numY) { yy ->
                MCell(xx, yy, 0)
            }
        }

        handleMatrix[0][0].value = 2
        handleMatrix[0][1].value = 2
        handleMatrix[0][2].value = 2
        handleMatrix[0][3].value = 2
        handleMatrix[2][2].value = 2
        handleMatrix[2][3].value = 2
        println("original:")
        printHandleMatrix(handleMatrix)

        val direction = M2048.MOVE_LEFT
        val horizontal = direction == M2048.MOVE_LEFT || direction == M2048.MOVE_RIGHT

//        for (xx in (0 until handleMatrix.size)) {
//            for (yy in (0 until handleMatrix[0].size)) {
//                if (horizontal) {
//                    val isLeft = direction == M2048.MOVE_LEFT
//                    val y = if (isLeft) yy else M2048.numY - yy - 1
//                    if (handleMatrix[xx][y].value == 0) {
//                        val range = if (isLeft) y until M2048.numY else y downTo 0
//                        for (i in range) {
//                            if (handleMatrix[xx][i].value != 0) {
//                                handleMatrix[xx][i].step++
//                            }
//                        }
//                    }
//                } else {
//                    val isUp = direction == M2048.MOVE_UP
//                    val x = if (isUp) xx else M2048.numX - xx - 1
//                    if (handleMatrix[x][yy].value == 0) {
//                        val range = if (isUp) x until M2048.numX else x downTo 0
//                        for (i in range) {
//                            if (handleMatrix[i][yy].value != 0) {
//                                handleMatrix[i][yy].step++
//                            }
//                        }
//                    }
//                }
//            }
//        }
        println()
        println("transform:")
        printHandleMatrix(handleMatrix)

        copyMatrix(handleMatrix, forwardMatrix)

//        forEachMatrix(forwardMatrix) { xx, yy ->
//            if (horizontal) {
//                val cell = forwardMatrix[xx][yy]
//                if (cell.step != 0) {
//                    val y = yy - cell.step
//                    forwardMatrix[xx][y].value = cell.value
//                    cell.value = 0
//                }
//            }
//        }

        println()
        println("[0,0]:${handleMatrix[0][0]}")
        println("handleMatrix:")
        printHandleMatrix(handleMatrix)

        forEachMatrix(forwardMatrix) { xx, yy ->
            val cell = forwardMatrix[xx][yy]
            when (direction) {
                M2048.MOVE_LEFT -> {
                    if (cell.value != 0 && yy < forwardMatrix[xx].size - 1 && cell.value == forwardMatrix[xx][yy + 1].value) {
                        cell.value *= 2
                        forwardMatrix[xx][yy + 1].value = 0
                        if (yy != 0 && forwardMatrix[xx][yy - 1].value == 0) {
                            forwardMatrix[xx][yy - 1].value = cell.value
                            cell.value = 0
                        }
                    }
                }
                M2048.MOVE_RIGHT -> {
                    val end = 0
                    val nextY = yy - 1
                    if (cell.value != 0 && yy > end && cell.value == forwardMatrix[xx][nextY].value) {
                        cell.value *= 2
                        forwardMatrix[xx][nextY].value = 0
                        if (yy != 0 && forwardMatrix[xx][yy + 1].value == 0) {
                            forwardMatrix[xx][yy + 1].value = cell.value
                            cell.value = 0
                        }
                    }
                }
            }

        }

        println()
        println("transform:")
        printHandleMatrix(forwardMatrix)
    }

    private fun printHandleMatrix(handleMatrix: Array<Array<MCell>>) {
        handleMatrix.forEachIndexed { index, ints ->
            ints.forEach { print("${it.value},") }
            println()
        }
    }

    private fun forEachMatrix(handleMatrix: Array<Array<MCell>>, callback: (xx: Int, yy: Int) -> Unit) {
        for (xx in (0 until handleMatrix.size)) {
            for (yy in (0 until handleMatrix[0].size)) {
                callback.invoke(xx, yy)
            }
        }
    }

    private fun copyMatrix(from: Array<Array<MCell>>, to: Array<Array<MCell>>) {
        forEachMatrix(from) { xx, yy ->
            to[xx][yy].value = from[xx][yy].value
        }
    }

    private fun initMatrix() {
//        handleMatrix[0][0].value = 2
//        handleMatrix[0][1].value = 2
//        handleMatrix[0][2].value = 2
//        handleMatrix[0][3].value = 2
//        handleMatrix[1][1].value = 2
//        handleMatrix[1][2].value = 2
//        handleMatrix[1][3].value = 2
//        handleMatrix[2][0].value = 2
//        handleMatrix[2][3].value = 2
//        handleMatrix[3][1].value = 2
//        handleMatrix[3][2].value = 2
//        handleMatrix[3][3].value = 2

        handleMatrix[0][0].value = 16
        handleMatrix[0][1].value = 2
        handleMatrix[0][2].value = 4
        handleMatrix[0][3].value = 2
        handleMatrix[1][0].value = 2
        handleMatrix[2][0].value = 4
        handleMatrix[3][0].value = 2
//        handleMatrix[1][3].value = 2
//        handleMatrix[1][0].value = 2
//        handleMatrix[2][2].value = 2
    }

    @Test
    fun ff() {
        initMatrix()
        var direction = M2048.MOVE_UP
        println("origin:")
//        printHandleMatrix2()
        printHandleMatrix(handleMatrix)
        formatHandleMatrix(direction)
        println("formatHandleMatrix:")
        printHandleMatrix2()

        refreshHandleMatrix(direction)
        println("refreshHandleMatrix:")
        printHandleMatrix2()
    }

    private fun refreshHandleMatrix(direction: Int) {
        val horizontal = direction == M2048.MOVE_LEFT || direction == M2048.MOVE_RIGHT
        val sign = if (direction == M2048.MOVE_RIGHT || direction == M2048.MOVE_DOWN) 1 else -1

        forEachMatrix(handleMatrix) { xx, yy ->
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
                handleMatrix[mx][my].value += cell.value
                cell.value = 0
            }

            cell.clear()
        }

        println("tempMatrix:")
        printHandleMatrix(handleMatrix)
    }

    private fun formatHandleMatrix(direction: Int) {
        val horizontal = direction == M2048.MOVE_LEFT || direction == M2048.MOVE_RIGHT

        fun followedEachAdd(xx: Int, yy: Int, isLeftOrUp: Boolean) {
            if (horizontal) {
                val range = if (isLeftOrUp) yy + 1 until handleMatrix[yy].size else yy - 1 downTo 0
                range.filter { handleMatrix[xx][it].value != 0 }
                        .forEach {
                            val other = handleMatrix[xx][it]
                            if (xx == 0 && it == 0) {
                                println("add ++ xx:$xx,yy:${yy + 1}")
                            }
                            if (other.mergeCell == null) {
                                other.movedY += 1
                            }
                        }
            } else {
                val range = if (isLeftOrUp) xx + 1 until handleMatrix[xx].size else xx - 1 downTo 0
                range.filter { handleMatrix[it][yy].value != 0 }
                        .forEach {
                            val other = handleMatrix[it][yy]
                            if (other.mergeCell == null) {
                                other.movedX += 1
                            }
                        }
            }

        }

        forEachMatrix(handleMatrix) { xx, yy ->

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
//                        if (xx == 0 && currentCellY == 3) {
//                            println(" in in")
//                        }
                        if (abs(nextCellY - currentCellY) > 1) {
                            val start = if (isLeft) nextCellY + 1 else currentCellY
                            val end = if (isLeft) currentCellY else nextCellY - 1
                            println("start:$start,end:$end")
                            for (i in (start until end)) {
                                if (handleMatrix[xx][i].value != 0) {
                                    checkBetweenIsZero = false
                                    break
                                }
                            }
                        }

                        if (checkBetweenIsZero && handleMatrix[xx][nextCellY].value == cell.value && handleMatrix[xx][nextCellY].mergeCell == null && cell.mergeCell == null) {
                            cell.movedY += 1
                            cell.mergeCell = handleMatrix[xx][nextCellY]
                            handleMatrix[xx][nextCellY].mergeCell = cell
                            handleMatrix[xx][nextCellY].mergeFirst = true
                            followedEachAdd(xx, currentCellY, isLeft)
                            println("当前单元格:(xx:$xx,yy:$currentCellY,value:${handleMatrix[xx][currentCellY].value}mergeFirst:${handleMatrix[xx][currentCellY].mergeFirst}),要合并的单元格:(xx:$xx,yy:$nextCellY,value:${handleMatrix[xx][nextCellY].value},mergeFirst:${handleMatrix[xx][nextCellY].mergeFirst})")
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
                            if (currentCellX == 3 && yy == 0) {
                                println("start:$start,end:$end")
                            }
                            for (i in (start until end)) {
                                if (handleMatrix[i][yy].value != 0) {
                                    checkBetweenIsZero = false
                                    break
                                }
                            }
                        }

                        if (checkBetweenIsZero && handleMatrix[nextCellX][yy].value == cell.value && handleMatrix[nextCellX][yy].mergeCell == null && cell.mergeCell == null) {
                            cell.movedX += 1
                            cell.mergeCell = handleMatrix[nextCellX][yy]
                            handleMatrix[nextCellX][yy].mergeCell = cell
                            handleMatrix[nextCellX][yy].mergeFirst = true
                            followedEachAdd(currentCellX, yy, isUp)
                            println("当前单元格:(xx:$currentCellX,yy:$yy,value:${handleMatrix[currentCellX][yy].value},mergeFirst:${handleMatrix[currentCellX][yy].mergeFirst}),要合并的单元格:(xx:$nextCellX,yy:$yy,value:${handleMatrix[nextCellX][yy].value},mergeFirst:${handleMatrix[nextCellX][yy].mergeFirst})")
                        }
                        nextCellX += if (isUp) -1 else 1
                    }
                }
            }
        }
    }

    private fun printHandleMatrix2() {
        handleMatrix.forEachIndexed { index, ints ->
            ints.forEach { print("(x:${it.x},y:${it.y},value:${it.value},movedX:${it.movedX},movedY:${it.movedY}),") }
            println()
        }
    }


    @Test
    fun gg() {

//        val length = 80
//        println("30radian:${Math.toRadians(30.0)}")
//
//        println("对边:${length * sin(Math.toRadians(30.0))}")
//
//        println("sin30:${sin(Math.toRadians(30.0))},asin:${asin(0.5)},sinh:${sinh(0.5)}")
//
//        println("a:${Math.toDegrees(asin(0.5))}")
//        println("Math.asin:${Math.asin(0.5)}")

//        println(log(8f, 2f))

//        val colorHelper=ColorHelper()
        print(4095.toHex())
    }


}
