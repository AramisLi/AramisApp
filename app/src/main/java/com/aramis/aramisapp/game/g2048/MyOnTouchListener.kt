package com.aramis.aramisapp.game.g2048

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

    private var isPrint=false

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

//        if (!isPrint){
//            isPrint=true
//            logE("event.action:${event.action},view.moveAnimator.isRunning:${view.moveAnimator.isRunning}")
//        }
        if (!view.moveAnimator.isRunning){

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    previousX = x
                    previousY = y
                }
                MotionEvent.ACTION_MOVE -> {
//                    logE("in move:$moved")
                    if (!moved){
                        val dx = previousX - x
                        val dy = previousY - y
//                        logE("moved dx:$dx,dy:$dx")
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
                    isPrint=false
//                    logE("moved重置,MotionEvent.ACTION_UP:${MotionEvent.ACTION_UP}")
                }
            }
        }

        if (event.action== MotionEvent.ACTION_UP){
            previousX = 0f
            previousY = 0f
            moved = false
            isPrint=false
//            logE("moved重置,MotionEvent.ACTION_UP:${MotionEvent.ACTION_UP}")
        }


        return true
    }
}