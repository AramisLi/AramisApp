package com.aramis.aramisapp

import com.aramis.aramisapp.game.sudoku.SudokuUtil
import org.junit.Test

import org.junit.Assert.*
import java.util.*
import kotlin.collections.RandomAccess
import kotlin.math.asin
import kotlin.math.sin
import kotlin.math.sinh

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
        var seeCount=0
        for (i in 0..8){
            for (j in 0..8){
                if (randomSudokuQuestion.second[i][j]>0){
                    seeCount++
                }
            }
        }
        println("随机数独问题共有${seeCount}个可见数")

        val solutions=sudokuUtil.checkMultiSolve(randomSudokuQuestion.second)
        println("此问题有${solutions?.size}个解")
        solutions?.apply {
            for(i in this){
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
        val a= listOf(1,2,3,4,5)
        val b= listOf(1,2,3,4,5)

        println(a==b)
    }

    @Test
fun dd(){
        var timeStr="00:00:00"
        val second = timeStr.subSequence(timeStr.lastIndexOf(":") + 1, timeStr.length)
        println(second)
    }
}
