package hello.com.aramis.reactnative.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aramis.library.extentions.logE
import com.aramis.library.extentions.toMD5
import com.aramis.library.http.ArRxVolley
import com.kymjs.rxvolley.client.HttpCallback
import com.kymjs.rxvolley.client.HttpParams
import hello.com.aramis.reactnative.R
import kotlinx.android.synthetic.main.layout_native_kotlin.*

/**
 *Created by Aramis
 *Date:2018/9/5
 *Description:
 */
class NativeActivity : AppCompatActivity() {
    private val url1 = "http://192.168.40.6:5001/index"
    private val url2 = "http://192.168.40.6:5001/maoyan/board"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_native_kotlin)

        btn_test1.setOnClickListener {
            testApi(url1, null)
        }
        btn_test2.setOnClickListener {
            val params = HttpParams()
            val time_stamp = (System.currentTimeMillis() / 1000).toInt()
            params.put("time_stamp", time_stamp)
            params.put("page_num", 0)
            params.put("page_size", 10)
            val sign = ("我是大帅哥$time_stamp").toMD5()
            params.put("sign", sign)

            testApi(url2, params)
        }
    }

    private fun testApi(url: String, params: HttpParams?) {

        ArRxVolley.get(url, params, object : HttpCallback() {
            override fun onSuccess(t: String?) {
                super.onSuccess(t)
                text_result.text = t
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(errorNo: Int, strMsg: String?) {
                super.onFailure(errorNo, strMsg)

                text_result.text = "error errorNo:$errorNo,strMsg:$strMsg"
            }
        }, false)
    }
}