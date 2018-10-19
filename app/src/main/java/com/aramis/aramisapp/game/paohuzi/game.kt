package com.aramis.aramisapp.game.paohuzi

import com.aramis.aramisapp.game.paohuzi.bean.Pai

/**
 *Created by Aramis
 *Date:2018/10/10
 *Description:
 */
class Game(val playerCount: Int) {
    private val names = arrayOf("一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
            "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾")

    fun getPaiList(): List<Pai> {
        var index = 0
        return (0 until 80).map {
            if (it != 0 && it % 4 == 0) {
                index++
            }
            Pai(names[index], index + 1, it + 1, index == 1 || index == 6 || index == 9 || index == 11 || index == 16 || index == 19)
        }
    }

    /**
     * 整理牌
     */
    fun sortPais(pais: MutableList<Pai>) {
        pais.sortBy { it.value }
        var index = 0

        while (index < pais.size) {
            val a = pais[index]
            val b = if (index + 1 < pais.size) pais[index + 1] else null
            val c = if (index + 2 < pais.size) pais[index + 2] else null
            val d = if (index + 3 < pais.size) pais[index + 3] else null
            if (a.value == b?.value && b.value == c?.value) {
                a.canMoved = false
                b.canMoved = false
                c.canMoved = false
                index += 3
                if (a.value == d?.value) {
                    index++
                    d.canMoved = false
                }
            } else {
                index++
            }
        }
    }

    /**
     * 是否是红牌
     */
    fun isRedPai(pai: Pai): Boolean {
        return pai.value == 2 || pai.value == 7 || pai.value == 10 || pai.value == 12 || pai.value == 17 || pai.value == 20
    }

    /**
     * 是否是大字
     */
    fun isBigPai(pai: Pai): Boolean {
        return pai.value > 10
    }

    fun isSuccess(pai: Pai, paiList: List<Pai>): Boolean {
        val result = paiList.toMutableList()
        result.add(pai)
        result.sortBy { it.value }

        var hasFour = false
        for (i in 0 until result.size / 3) {

            val a = result[0 + i * 3]
            val b = result[1 + i * 3]
            val c = result[2 + i * 3]

            if (a == b && b == c) {
                if (i != result.size / 3 - 1) {
                    val d = result[3 + i * 3]
                    if (a == d) {
                        hasFour = true
                        break
                    }
                }
                continue
            } else if (b.value - a.value == 1 && c.value - b.value == 1) {
                continue
            } else {
                return false
            }
        }
        if (hasFour) {
            val fResult = mutableListOf<Pai>()
            fResult.addAll(result)

        }
        return true
    }
}