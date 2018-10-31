package com.aramis.aramisapp.pendulum

import android.graphics.Color

/**
 *Created by Aramis
 *Date:2018/10/29
 *Description:
 */
class ColorHelper {

    fun getColorStr(progress: Int): String {
        return if (progress < 255) {
            val a = toHex(progress / 16)
            val b = toHex(progress % 16)
            "#$a$b$a$b$a$b"
        } else {
            "#000000"
        }

    }

    fun getColor(str: String): Int {
        return Color.parseColor(str)
    }

    fun toHex(a: Int): String {

        fun toHex16(n: Int): String {
            return if (n < 10) {
                n.toString()
            } else {
                when (n) {
                    10 -> "A"
                    11 -> "B"
                    12 -> "C"
                    13 -> "D"
                    14 -> "E"
                    else -> "F"
                }
            }
        }
        return if (a < 16) {
            toHex16(a)
        } else {
            val b = a / 16
            toHex(b) + toHex16(a % 16)
        }
    }
}