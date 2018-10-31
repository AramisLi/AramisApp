package com.aramis.aramisapp.pendulum

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.aramis.aramisapp.R
import org.jetbrains.anko.dip
import org.jetbrains.anko.textColor

/**
 *Created by Aramis
 *Date:2018/10/29
 *Description:
 */
class SeekBarLayout : LinearLayout {
    private lateinit var seekBar: SeekBar
    private lateinit var headTextView: TextView
    private lateinit var footTextView: TextView

    private var headName = ""
    private var footName = ""
    private var max = 100
    private var progress = 0

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        initType(attrs, defStyleAttr)
        seekBar = SeekBar(context)

        headTextView = TextView(context)
        footTextView = TextView(context)
        val textLayoutParams = LinearLayout.LayoutParams(dip(60), dip(40))
        headTextView.text = headName
        headTextView.layoutParams = textLayoutParams
        headTextView.gravity = Gravity.CENTER
        headTextView.textColor = 0xff333333.toInt()
        footTextView.text = footName
        footTextView.layoutParams = textLayoutParams
        footTextView.gravity = Gravity.CENTER
        footTextView.textColor = 0xff333333.toInt()

        seekBar.max = max
        seekBar.progress=progress
        val seekLayoutParams = LinearLayout.LayoutParams(dip(0), dip(40), 1f)
        seekBar.layoutParams = seekLayoutParams

        this.orientation = LinearLayout.HORIZONTAL
        this.addView(headTextView)
        this.addView(seekBar)
        this.addView(footTextView)
    }

    fun setOnSeekBarChangeListener(l:SeekBar.OnSeekBarChangeListener){
        seekBar.setOnSeekBarChangeListener(l)
    }

    fun setSeekBarOnTouchListener(l:View.OnTouchListener){
        seekBar.setOnTouchListener(l)
    }

    fun getPregress():Int{
        return seekBar.progress
    }

    private fun initType(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SeekBarLayout, defStyleAttr, 0)
        max = typedArray.getInt(R.styleable.SeekBarLayout_max, 100)
        progress = typedArray.getInt(R.styleable.SeekBarLayout_progress, 0)
        headName = typedArray.getString(R.styleable.SeekBarLayout_headName) ?: "0"
        footName = typedArray.getString(R.styleable.SeekBarLayout_footName) ?: max.toString()
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