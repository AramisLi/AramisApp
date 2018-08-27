package com.aramis.aramisapp

import org.junit.Test

import org.junit.Assert.*
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
    fun sanjiao(){
        val a=3
        val b=4
        val c=5

        val alpha=sin(Math.toRadians(60.0))

        asin(0.5)

        println(alpha)
        println(Math.toDegrees(asin(0.5)))
        println(Math.toDegrees(sinh(0.5)))
    }
}
