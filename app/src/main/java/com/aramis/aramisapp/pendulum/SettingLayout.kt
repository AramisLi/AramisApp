package com.aramis.aramisapp.pendulum

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.aramis.aramisapp.R
import com.aramis.library.extentions.logE
import org.jetbrains.anko.dip
import org.jetbrains.anko.displayMetrics
import kotlin.math.max

/**
 *Created by Aramis
 *Date:2018/10/23
 *Description:
 */
class SettingLayout : RelativeLayout {

    private var settingButtonX = 0
    var settingButtonY = 0
    var settingRadius = 0
    var settingButtonColor = 0
    private var settingButtonSize = 0

    //View
    private lateinit var settingLinearLayout: LinearLayout
    private lateinit var settingView: SettingView
    //default
    private val defaultSettingViewLayoutHeight = dip(150)

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        initType(attrs, defStyleAttr)
        initDimension()

        settingLinearLayout = LinearLayout(context)
        settingLinearLayout.orientation = LinearLayout.VERTICAL
        settingLinearLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, defaultSettingViewLayoutHeight)

//        settingView = SettingView(context)
//        settingView.settingRadius = settingRadius
//        settingView.settingButtonColor = settingButtonColor


        val textView=getSettingView()
        this.addView(textView)
        this.addView(settingLinearLayout)

    }

//    override fun onFinishInflate() {
//        super.onFinishInflate()
//
//        val textView=getSettingView()
//        this.addView(textView)
//        this.addView(settingLinearLayout)
//    }

    private fun getSettingView(): TextView {
        val textView = TextView(context)
        val layoutParams = RelativeLayout.LayoutParams(settingButtonSize, settingButtonSize)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        layoutParams.setMargins(0, 0, width - settingButtonX, height - settingButtonY)
        textView.background = context.getDrawable(R.drawable.back_setting_view)
        textView.layoutParams = layoutParams

        return textView
    }

    fun addSettingView(view: View) {
        settingLinearLayout.addView(view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val h = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            max(MeasureSpec.getSize(heightMeasureSpec), defaultSettingViewLayoutHeight)
        } else defaultSettingViewLayoutHeight

//        logE("width:$width,height:$height,defaultSettingViewLayoutHeight:$defaultSettingViewLayoutHeight,h:$h,dip(150):${dip(150)}")
        setMeasuredDimension(context.displayMetrics.widthPixels, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        initDimension()
    }


    private fun initDimension() {
        settingRadius = if (settingRadius == 0) dip(20) else settingRadius
        settingButtonSize = settingRadius * 2
        settingButtonX = if (settingButtonX == 0) width - dip(20) - settingButtonSize else settingButtonX
        settingButtonY = if (settingButtonY == 0) height - dip(40) - settingButtonSize else settingButtonY
        settingButtonColor = if (settingButtonColor == 0) 0xff457EE9.toInt() else settingButtonColor

    }

    private fun initType(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SettingView, defStyleAttr, 0)
        settingButtonX = typedArray.getInt(R.styleable.SettingView_settingButtonX, 0)
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