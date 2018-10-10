package com.aramis.aramisapp.game.sudoku

import com.aramis.library.extentions.logE

/**
 *Created by Aramis
 *Date:2018/9/28
 *Description:
 */
class SudokuUtil {

    private var sudoku = mutableListOf<MutableList<Int>>()

    /**
     * 随机出题
     * @param seeCount:可见数的个数，最少17个
     */
    fun getRandomSudokuQuestion(seeCount: Int): Pair<List<List<Int>>, List<List<Int>>> {
        val question = mutableListOf<MutableList<Int>>()
        if (seeCount < 17) return question to question
        //重置本地属性
        sudoku = mutableListOf<MutableList<Int>>()
        //获取随机满填数独
        val answer = getRandomSudoku()
        //数字个数列表
        val numberCountList = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

        val randomPositionList = getRandomPositionList(seeCount).toMutableList()
        for (p in randomPositionList) {
            val number = answer[p.first][p.second]
            numberCountList[number - 1]++
        }
//        println("数字个数列表:$numberCountList")

        fun checkAllNumberExist(): Pair<Int, Int>? {
            if (0 in numberCountList) {
                val number = numberCountList.indexOf(0) + 1
                for (x in 0..8) {
                    for (y in 0..8) {
                        if (answer[x][y] == number) {
//                            println("次数为0的数字:$number,x:$x,y:$y")
                            return x to y
                        }
                    }
                }

            }
            return null
        }

        //所有数字都出现至少一次
        val allExistPair = checkAllNumberExist()
        allExistPair?.apply {
            val maxIndex = numberCountList.indexOf(numberCountList.max())
            val maxNumber = maxIndex + 1

            for (i in randomPositionList) {
                if (answer[i.first][i.second] == maxNumber) {
                    randomPositionList.remove(i)
                    break
                }
            }
            numberCountList[maxIndex]--
            numberCountList[numberCountList.indexOf(0)]++
            randomPositionList.add(this)
        }

        //赋值
        for (i in 0 until answer.size) {
            val l = mutableListOf<Int>()
            for (j in 0 until answer[i].size) {
                if (i to j in randomPositionList) {
                    l.add(answer[i][j])
                } else {
                    l.add(0)
                }
            }
            question.add(l)
        }

        //检查唯一解的重复次数
        var checkTimes = 0

        while (true) {
            checkTimes++

            //检查唯一解
            val originSolutions = checkMultiSolve(question)
            if (originSolutions?.size == 1) {
                break
            } else {
                if (originSolutions == null) {
                    println("无解")
                    break
                } else {
                    println("存在多解 ${originSolutions.size}")
                    //所有关键点的位置
                    val diffPairList = checkMultiSolveDiffPositions(answer.toMutableList(), originSolutions.toMutableList())
                    //更换关键点
                    diffPairList?.apply {
                        //随机关键点
                        val p1 = this[(0 until this.size).shuffled().first()]
                        //出现最多的数字
                        val maxIndex = numberCountList.indexOf(numberCountList.max())
                        val maxNumber = maxIndex + 1
                        //出现最多数字的随机位置
                        var maxP = getNumberRandomPosition(answer, maxNumber,randomPositionList)
                        while (question[maxP.first][maxP.second] == 0) {
                            maxP = getNumberRandomPosition(answer, maxNumber,randomPositionList)
                        }
                        question[maxP.first][maxP.second] = 0

                        question[p1.first][p1.second] = answer[p1.first][p1.second]
                        numberCountList[maxIndex]--
                        numberCountList[question[p1.first][p1.second] - 1]++
                        randomPositionList.remove(maxP)
                        randomPositionList.add(p1)
//                        println("numberCountList:$numberCountList")
                    }
                }
            }
        }

        println("检查唯一解共${checkTimes}次")
//        logE("$answer")
        return answer to question
    }

    //获取一个数的随机位置
    private fun getNumberRandomPosition(list: List<List<Int>>, number: Int,randomPositionList:List<Pair<Int,Int>>): Pair<Int, Int> {
        return randomPositionList.filter {  list[it.first][it.second]==number}.shuffled().first()
    }

    //找出多解中，正确解与其他解都不同的项的位置
    private fun checkMultiSolveDiffPositions(answerList: MutableList<List<Int>>, multiSolveList: MutableList<List<List<Int>>>): List<Pair<Int, Int>>? {
        var answerPosition = -1
        for (i in 0 until multiSolveList.size) {
            if (multiSolveList[i][0] == answerList[0]) {
                answerPosition = i
                break
            }
        }
        if (answerPosition != -1) {
            val rightList = multiSolveList.removeAt(answerPosition)
            val pairList = mutableListOf<Pair<Int, Int>>()
            for (x in 0..8) {
                for (y in 0..8) {
                    var diffCount = 0
                    for (list in multiSolveList) {
                        if (list[x][y] != rightList[x][y]) {
                            diffCount++
                        }
                    }
                    if (diffCount == multiSolveList.size) {
                        pairList.add(x to y)
                    }
                }
            }

            return if (pairList.isEmpty()) {
                println("没有关键点")
                null
            } else {
//                println("关键位置:")
//                println(pairList)
                pairList
            }

        } else {
            return null
        }
    }

    //获取一个随机的位置列表
    private fun getRandomPositionList(seeCount: Int): MutableList<Pair<Int, Int>> {
        val min = 1
        val max = if (((seeCount / 9) + 2)>9) 9 else (seeCount / 9) + 2
        val timeArr = mutableListOf<Int>()
        val result = mutableListOf<Pair<Int, Int>>()
        while (true) {
            (0..3).forEach {
                val sx = if (it == 3) 3 else 0
                val sy = if (it == 3) 0 else it * 3
                val time = ((if (1 in timeArr) min + 1 else min)..max).shuffled().first()
                timeArr.add(time)
//                println("sx:$sx,sy:$sy,time:$time")
                var tempTime = 0
                while (tempTime < time) {
                    val rx = (sx until sx + 3).shuffled().first()
                    val ry = (sy until sy + 3).shuffled().first()

                    if (rx to ry !in result) {
//                        println("rx:$rx,ry:$ry")
                        result.add(rx to ry)
                        result.add(8 - rx to 8 - ry)
                        tempTime++
                    }
                }
            }
            //验证每一宫至少有一个数且中宫的数量不能大于9
//            println("seeCount:$seeCount,result.size:${result.size}")
            if (seeCount - result.size <= 9) {
                break
            } else {
                result.clear()
            }
        }

        when {
            //生成的数少,中央添加
            result.size < seeCount -> {
                val times = seeCount - result.size
//                println("生成的数少,中央添加 seeCount:$seeCount,result.size:${result.size},times:$times")
                val sx = 3
                val sy = 3
                var tempTime = 0
                while (tempTime < times) {
                    val x = (sx until sx + 3).shuffled().first()
                    val y = (sy until sy + 3).shuffled().first()
                    if (x to y !in result) {
//                        println("add x:$x,add y:$y")
                        result.add(x to y)
                        tempTime++
                    }

                }
            }
            //生成的数多，随机删除
            result.size > seeCount -> {
                val times = result.size - seeCount
//                println("生成的数多，随机删除。seeCount:$seeCount,result.size:${result.size},times:$times")
                var tempTime = 0
                while (tempTime < times) {
                    val index = (0 until result.size).shuffled().first()
                    result.removeAt(index)
                    tempTime++
                }
            }
            else -> println("相等，不做处理")
        }

        return result
    }

    private fun printPositionList(l: MutableList<Pair<Int, Int>>) {
        var a = 0
        for (i in l) {
            print(i)
            print(",")
            a = if (a < 4) a + 1 else {
                println()
                0
            }
        }
        println()
    }

    //检查每一宫至少有一个数字
    private fun checkPerBlockHanOne(positionList: MutableList<Pair<Int, Int>>): Boolean {
        fun check(xRange: IntRange, yRange: IntRange): Boolean {
            for (i in positionList) {
                if (i.first in xRange && i.second in yRange) {
                    return true
                }
            }
            return false
        }
        for (i in 0..8) {
            val line = i % 3
            if (!check(line * 3..line * 3 + 2, i % 3 * 3..i % 3 * 3 + 2)) {
                return false
            }
        }

        return true
    }

    /**
     * 得到一个随机数独
     */
    fun getRandomSudoku(): List<List<Int>> {
        val result = mutableListOf<MutableList<Int>>()
        fun addZeroList() {
            result.add((0..8).map { 0 }.toMutableList())
            result.add((0..8).map { 0 }.toMutableList())
        }

        fun getCheckedIntList(): MutableList<Int> {
            var la = getRandomIntList()
            var times = 0
            while (true) {
                var b = true
                f@ for (i in 0 until result.size) {
                    for (j in 0 until result[i].size) {
                        times++
                        if (result[i][j] == 0) {
                            break
                        } else {
                            if (result[i][j] == la[j]) {
                                b = false
                                break@f
                            }
                        }
                    }
                }
                if (b) {
                    break
                } else {
                    la = getRandomIntList()
                }
            }
//            println("times:$times")
            return la

        }

        result.add(getRandomIntList())
        addZeroList()
        result.add(getCheckedIntList())
        addZeroList()
        result.add(getCheckedIntList())
        addZeroList()
//        println("times:$result")
        return this.solve(result)
    }

    private fun getRandomIntList(): MutableList<Int> {
        return (1..9).shuffled().toMutableList()
    }

    //检查多解数独
    fun checkMultiSolve(sudoku: List<List<Int>>): List<List<List<Int>>>? {
        if (sudoku.size < 9 || sudoku[0].size < 9) {
            return null
        }

        fun getNNext(): Pair<Int, Int> {
            for (i in 0 until sudoku.size) {
                for (j in 0 until sudoku[i].size) {
                    if (sudoku[i][j] == 0) {
                        return i to j
                    }
                }
            }
            return -1 to -1
        }
        val (x, y) = getNNext()
        val result = mutableListOf<List<List<Int>>>()

        for (i in 1..9) {
            if (check(x, y, i, sudoku)) {
                this.sudoku.clear()
                for (j in sudoku) {
                    this.sudoku.add(j.toMutableList())
                }
                this.sudoku[x][y] = i

                this.solveNative(this.sudoku, resetNativeSudoku = false)

                var isJie = true
                for (k in this.sudoku) {
                    if (0 in k) {
                        isJie = false
                        break
                    }
                }

                if (isJie) {
                    val l = mutableListOf<MutableList<Int>>()
                    l.addAll(this.sudoku)
                    println(l)
                    result.add(l)
                }
            }
        }


        return result
    }

    private fun solveNative(sudoku: List<List<Int>>, resetNativeSudoku: Boolean = true): MutableList<MutableList<Int>> {
        if (sudoku.size < 9 || sudoku[0].size < 9) {
            return this.sudoku
        }
        if (resetNativeSudoku) {
            this.sudoku.clear()
            for (i in sudoku) {
                this.sudoku.add(i.toMutableList())
            }
        }
        if (this.sudoku[0][0] == 0) {
            this.recursionSolve(0, 0)
        } else {
            val p = getNext(0, 0)
            this.recursionSolve(p.first, p.second)
        }

        return this.sudoku


    }


    fun solve(sudoku: List<List<Int>>): MutableList<MutableList<Int>> {
        return solveNative(sudoku)
    }

    private fun recursionSolve(x: Int, y: Int): Boolean {
        if (this.sudoku[x][y] == 0) {
            for (i in 1..9) {
                if (this.check(x, y, i)) {
                    this.sudoku[x][y] = i
                    val (nextX, nextY) = this.getNext(x, y)
                    if (nextX == -1) {
                        return true
                    } else {
                        val r = recursionSolve(nextX, nextY)
                        if (r) {
                            return true
                        } else {
                            this.sudoku[x][y] = 0
                        }
                    }
                }
            }
        }
        return false
    }

    fun checkInput(n:Int,matrix:List<List<Int>>){

    }

    private fun check(x: Int, y: Int, n: Int, ss: List<List<Int>>? = null): Boolean {
        val s = ss ?: this.sudoku
        //行内存在
        if (n in s[x]) {
            return false
        }
        //列内存在
        for (i in s) {
            if (i[y] == n) {
                return false
            }
        }
        //宫内存在
        val gx = (x / 3) * 3
        val gy = (y / 3) * 3

        for (i in gx until gx + 3) {
            for (j in gy until gy + 3) {
                if (s[i][j] == n) {
                    return false
                }
            }

        }

        return true
    }

    private fun getNext(x: Int, y: Int): Pair<Int, Int> {
        for (i in x until this.sudoku.size) {
            val start = if (i == x) y + 1 else 0
            //如果start>this.sudoku[i].size,循环条件不满足。不会数组越界
            for (j in start until this.sudoku[i].size) {
                if (this.sudoku[i][j] == 0) {
                    return i to j
                }
            }
        }

        return -1 to -1
    }

    fun solve(sudoku: Array<Array<Int>>) {
        val aSudoku = mutableListOf<List<Int>>()
        sudoku.forEach { aSudoku.add(it.toList()) }
        solve(aSudoku)
    }

}