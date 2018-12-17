package fcom.aramisapp.base

import com.aramis.library.base.BaseActivity
import com.aramis.library.extentions.logE
import com.google.gson.Gson
import com.kymjs.rxvolley.RxVolley
import com.kymjs.rxvolley.client.HttpCallback
import org.json.JSONObject

/**
 *Created by Aramis
 *Date:2018/7/27
 *Description:
 */
abstract class AraBaseActivity :BaseActivity() {

    abstract override fun getPresenter(): AraBasePresenter<*>?

    inline fun <reified T> doGet(url: String, crossinline success: (t: T) -> Unit,
                                 crossinline fail: (errorNo: Int, strMsg: String?) -> Unit) {
        val clazz = T::class.java
        RxVolley.get(url, object : HttpCallback() {
            override fun onSuccess(t: String?) {
                super.onSuccess(t)
                logE("请求成功 $t")
                val obj = JSONObject(t)
                val errno = obj.getInt("errno")
                if (errno == 0) {
                    val dataStr = obj.getString("data")
                    logE("ddd $dataStr")
                    val gson = Gson()
                    val bean = gson.fromJson(dataStr, clazz)
                    success.invoke(bean)
                } else {
                    val errmsg = obj.getString("errmsg")
                    fail.invoke(errno, "请求失败: $errmsg")
                }
            }

            override fun onFailure(errorNo: Int, strMsg: String?) {
                super.onFailure(errorNo, strMsg)
                fail.invoke(errorNo, strMsg)
            }
        })
    }
}