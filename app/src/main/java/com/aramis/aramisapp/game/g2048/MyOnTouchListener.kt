package com.aramis.aramisapp.game.g2048

import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import com.aramis.library.extentions.logE
import kotlin.math.abs

/**
 *Created by Aramis
 *Date:2018/10/16
 *Description:
 */
class MyOnTouchListener(val view: M2048View) : View.OnTouchListener {
    private val moveMin = 3
    private var previousX = 0f
    private var previousY = 0f
    private var moved = false

    private var isPrint = false

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        when (view.state) {
            M2048View.M2048ViewState.Running -> onGameRunning(event)
            M2048View.M2048ViewState.Success -> onGameSuccess(event)
            M2048View.M2048ViewState.Fail -> onGameFailure(event)
        }

        return true
    }

    private fun onGameFailure(event: MotionEvent) {
        val x = event.x
        val y = event.y

        if (inRect(x, y, view.firstButtonRectF)) {
            if (M2048.debug){
                logE("reset button click")
            }
            view.onFailResetButtonClick(event)
        }

    }

    private fun onGameSuccess(event: MotionEvent) {
        val x = event.x
        val y = event.y

        if (inRect(x, y, view.firstButtonRectF)) {
            if (M2048.debug){
                logE("continue button click")
            }
            view.onSuccessContinueButtonClick(event)
        } else if (inRect(x, y, view.secondButtonRectF)) {
            if (M2048.debug){
                logE("reset button click")
            }
            view.onSuccessResetButtonClick(event)
        }
    }

    private fun inRect(x: Float, y: Float, rectF: RectF): Boolean {
        return x > view.startingX+rectF.left && x < view.startingX+rectF.right && y > view.startingY+rectF.top && y < view.startingY+rectF.bottom
    }

    private fun onGameRunning(event: MotionEvent) {
        val x = event.x
        val y = event.y
        if (!view.moveAnimator.isRunning) {

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    previousX = x
                    previousY = y
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!moved) {
                        val dx = previousX - x
                        val dy = previousY - y
                        when (true) {
                            //左移
                            abs(dx) > abs(dy) && dx > 0 && abs(dx) > moveMin -> {
                                moved = true
                                view.game.move(M2048.MOVE_LEFT)
                            }
                            //右移
                            abs(dx) > abs(dy) && dx < 0 && abs(dx) > moveMin -> {
                                moved = true
                                view.game.move(M2048.MOVE_RIGHT)
                            }
                            //上移
                            abs(dx) < abs(dy) && dy > 0 && abs(dy) > moveMin -> {
                                moved = true
                                view.game.move(M2048.MOVE_UP)
                            }
                            //下移
                            abs(dx) < abs(dy) && dy < 0 && abs(dy) > moveMin -> {
                                moved = true
                                view.game.move(M2048.MOVE_DOWN)
                            }

                        }
                    }

                }
                MotionEvent.ACTION_UP -> {
                    previousX = 0f
                    previousY = 0f
                    moved = false
                    isPrint = false
                }
            }
        }

        if (event.action == MotionEvent.ACTION_UP) {
            previousX = 0f
            previousY = 0f
            moved = false
            isPrint = false
        }
    }
}