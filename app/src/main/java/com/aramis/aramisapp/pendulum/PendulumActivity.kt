package com.aramis.aramisapp.pendulum

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import com.aramis.aramisapp.R
import com.aramis.library.extentions.logE
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import kotlinx.android.synthetic.main.activity_pendulum.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import org.jetbrains.anko.textColor

/**
 *Created by Aramis
 *Date:2018/10/23
 *Description:
 */
class PendulumActivity : AraBaseActivity() {
    private lateinit var inAnimation: TranslateAnimation
    private lateinit var outAnimation: TranslateAnimation
    private val duration = 500L
    private val colorHelper = ColorHelper()
    private var pendulumColor = 0
    private var isAnimRunning = false
    private var isOut = true
    private var isIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pendulum)

        setListener()
    }

    private fun setListener() {
        text_setting.setOnClickListener {
            startInAnim()
        }

        pendulumView.onAnimRunningTouchListener = {
            startOutAnim()
        }

        seek_ball_color.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                toColor(progress)?.apply {
                    //                    val colorStr = colorHelper.getColorStr(progress)
                    view_color.text = this
//                    logE("colorStr:$this")
                    pendulumColor = colorHelper.getColor(this)
                    layout_view_color.backgroundColor = pendulumColor
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                layout_view_color.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                layout_view_color.visibility = View.GONE
                pendulumView.pendulumColor = pendulumColor
            }

        })
        layout_setting.setOnClickListener { }

        seek_pendulum_length.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                pendulumView.pendulumLength = dip(50) + (dip(280) - dip(50)) * progress / 100f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        text_mode_pendulum.setOnClickListener {
            text_mode_newton.setBackgroundResource(R.drawable.setting_layout_unselect)
            text_mode_pendulum.setBackgroundResource(R.drawable.setting_layout_select)
            text_mode_pendulum.textColor=0xffffffff.toInt()
            text_mode_newton.textColor=0xffcccccc.toInt()
            pendulumView.mode = PendulumView.Mode.Pendulum
        }

        text_mode_newton.setOnClickListener {
            text_mode_pendulum.setBackgroundResource(R.drawable.setting_layout_unselect)
            text_mode_newton.setBackgroundResource(R.drawable.setting_layout_select)
            text_mode_pendulum.textColor=0xffcccccc.toInt()
            text_mode_newton.textColor=0xffffffff.toInt()
            pendulumView.mode = PendulumView.Mode.Newton
        }
    }

    //0-4095
    private fun toColor(progress: Int): String? {
        return if (progress in 0..4095) {
            val hex = colorHelper.toHex(progress)
            logE("hex")
            val a: String
            val b: String
            val c: String
            when (hex.length) {
                3 -> {
                    a = hex[0].toString()
                    b = hex[1].toString()
                    c = hex[2].toString()
                }
                2 -> {
                    a = "0"
                    b = hex[0].toString()
                    c = hex[1].toString()
                }
                else -> {
                    a = "0"
                    b = "0"
                    c = hex[0].toString()
                }
            }
            "#${a}0${b}0${c}0"
        } else null
    }

    override fun onStart() {
        super.onStart()
        initAnim()
    }

    private fun startInAnim() {
        if (!isAnimRunning && isOut) {
            logE("in in in")
            layout_setting.startAnimation(inAnimation)
        }
    }

    private fun startOutAnim() {
        if (!isAnimRunning && isIn) {
            layout_setting.startAnimation(outAnimation)
        }
    }

    private fun initAnim() {
        val layoutHeight = resources.getDimensionPixelSize(R.dimen.height_setting_layout)
        outAnimation = TranslateAnimation(0f, 0f, 0f, layoutHeight.toFloat())
        outAnimation.duration = duration
        outAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                layout_setting.visibility = View.GONE
                isAnimRunning = false
                isIn = false
                isOut = true
            }

            override fun onAnimationStart(animation: Animation?) {
                isAnimRunning = true
                isIn = false
                isOut = false
            }

        })

        inAnimation = TranslateAnimation(0f, 0f, layoutHeight.toFloat(), 0f)
        inAnimation.duration = duration
        inAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                isAnimRunning = false
                isIn = true
                isOut = false
            }

            override fun onAnimationStart(animation: Animation?) {
                layout_setting.visibility = View.VISIBLE
                isAnimRunning = true
                isIn = false
                isOut = false
            }

        })


    }


    override fun getPresenter(): AraBasePresenter? = null
}