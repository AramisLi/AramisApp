package com.aramis.aramisapp.game.g2048

import kotlin.math.max

/**
 *Created by Aramis
 *Date:2018/10/12
 *Description:
 */

class AnimationCell(x: Int, y: Int, val animationType: Int, length: Long, delay: Long, var extras: Array<Int>?) : Cell(x, y) {
    private var timeElapsed = 0L
    private val animationTime: Long = length
    private val delayTime = delay

    //计时
    fun tick(timeElapsed: Long) {
        this.timeElapsed += timeElapsed
    }

    //动画完成
    fun animationDone(): Boolean {
        return animationTime + delayTime < timeElapsed
    }

    //获取百分比
    fun getPercentageDone(): Double {
        return max(0.0, 1.0 * (timeElapsed - delayTime) / animationTime)
    }

    fun isActive(): Boolean {
        return timeElapsed >= delayTime
    }

}

class AnimationGrid(x: Int, y: Int) {
    val globalAnimation = mutableListOf<AnimationCell>()
    private val field = mutableListOf<MutableList<MutableList<AnimationCell>>>()
    private var activeAnimations = 0
    private var oneMoreFrame = false

    init {
        for (xx in (0 until x)) {
            field.add(mutableListOf())
            for (yy in (0 until y)) {
                field[xx].add(mutableListOf())
            }
        }
    }

    //开始动画
    fun startAnimation(x: Int, y: Int, animationType: Int, length: Long, delay: Long, extras: Array<Int>?) {
        val animationCell = AnimationCell(x, y, animationType, length, delay, extras)

        if (x == -1 && y == -1) {
            globalAnimation.add(animationCell)
        } else {
            field[x][y].add(animationCell)
        }
        activeAnimations++
    }

    //
    fun tickAll(timeElapsed: Long) {
        val cancelledAnimations = mutableListOf<AnimationCell>()
        for (animation in globalAnimation) {
            animation.tick(timeElapsed)
            if (animation.animationDone()) {
                cancelledAnimations.add(animation)
                activeAnimations--
            }
        }
        field.forEach { aaa ->
            aaa.forEach { aa ->
                aa.forEach { animation ->
                    animation.tick(timeElapsed)
                    if (animation.animationDone()) {
                        cancelledAnimations.add(animation)
                        activeAnimations--
                    }
                }
            }

        }

        for (cancelledAnimation in cancelledAnimations) {
            cancelAnimation(cancelledAnimation)
        }
    }

    private fun cancelAnimation(animation: AnimationCell) {
        if (animation.x == -1 && animation.y == -1) {
            globalAnimation.remove(animation)
        } else {
            field[animation.x][animation.y].remove(animation)
        }
    }

    fun isAnimationActive(): Boolean {
        return when {
            activeAnimations != 0 -> {
                oneMoreFrame = true
                true
            }
            oneMoreFrame -> {
                oneMoreFrame = false
                true
            }
            else -> false
        }
    }

    //获取动画列表
    fun getAnimationCell(x: Int, y: Int): List<AnimationCell> {
        return field[x][y]
    }


    fun cancelAnimations() {
        field.forEach { aaa ->
            aaa.forEach { aa ->
                aa.clear()
            }
        }
        globalAnimation.clear()
        activeAnimations = 0
    }


}








