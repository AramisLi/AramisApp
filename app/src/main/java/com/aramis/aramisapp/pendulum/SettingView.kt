package com.aramis.aramisapp.pendulum

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.aramis.aramisapp.R
import org.jetbrains.anko.dip

/**
 *Created by Aramis
 *Date:2018/10/23
 *Description:
 */
class SettingView : View {

    private var settingButtonSize = 0
    var settingRadius = 0
    var settingButtonColor = 0
    var settingsView: RelativeLayout? = null

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        initType(attrs, defStyleAttr)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initDimension()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    private fun initDimension() {
        settingRadius = if (settingRadius == 0) dip(20) else settingRadius
        settingButtonSize = settingRadius * 2
        settingButtonColor = if (settingButtonColor == 0) 0xff457EE9.toInt() else settingButtonColor

    }

    private fun initType(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SettingView, defStyleAttr, 0)
//        settingButtonX = typedArray.getInt(R.styleable.SettingView_settingButtonX, 0)
    }


    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

}