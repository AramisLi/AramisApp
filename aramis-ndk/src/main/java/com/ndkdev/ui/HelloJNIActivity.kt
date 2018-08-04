package com.ndkdev.ui

import android.annotation.SuppressLint
import android.os.Bundle
import com.ndkdev.R
import com.ndkdev.utils.NDKUtilsJ
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import kotlinx.android.synthetic.main.activity_hello_jni.*
import java.lang.Exception

/**
 *Created by Aramis
 *Date:2018/7/27
 *Description:
 */
class HelloJNIActivity : AraBaseActivity() {

    var param = 1
    val cc = NDKUtilsJ.getStringFromC()
    var testStr="HelloJNIActivity"
    private var cryptStr = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello_jni)

        setListener()
    }

    @SuppressLint("SetTextI18n")
    private fun setListener() {
        btn_load_c.setOnClickListener {
            text_jni_result.text = NDKUtilsJ.getStringFromC()
        }
        btn_load_cpp.setOnClickListener {
            text_jni_result.text = String(NDKUtilsJ.getChineseString("咋地啦").toByteArray(Charsets.UTF_16), Charsets.UTF_8)
        }
        btn_load_crypt.setOnClickListener {
            cryptStr = NDKUtilsJ.crypt("adc")
            text_jni_result.text = cryptStr
        }
        btn_load_decrypt.setOnClickListener {
            text_jni_result.text = NDKUtilsJ.decrypt(cryptStr)
        }
        btn_load_byte.setOnClickListener {
            try {
                text_jni_result.text = String(NDKUtilsJ.getByteArray().toByteArray())
            } catch (e: Exception) {
                text_jni_result.text = "错误"
            }
        }
        btn_load_exception.setOnClickListener {
            try {
                val str = NDKUtilsJ.testException()
                text_jni_result.text = str
            } catch (e: Exception) {
                val s= "catch到错误 ${e.javaClass.name} ${e.message}"
                text_jni_result.text = s
            }
        }
    }

    override fun getPresenter(): AraBasePresenter? = null
}