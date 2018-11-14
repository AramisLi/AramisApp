package fcom.aramisapp.base

import com.aramis.library.base.BasePresenter
import com.aramis.library.http.ArHttpUtils
import com.aramis.library.http.ArRxVolley
import com.kymjs.rxvolley.RxVolley
import com.kymjs.rxvolley.client.HttpCallback
import com.kymjs.rxvolley.client.HttpParams
import com.kymjs.rxvolley.client.ProgressListener

/**
 *Created by Aramis
 *Date:2018/7/27
 *Description:
 */
open class AraBasePresenter<T : AraBaseView>(view: T) : BasePresenter<T>(view) {

    protected fun getHttpParams(data: Map<String, Any>?, useToken: Boolean = true): HttpParams {
        val jsonHttpParams = ArHttpUtils.getJsonHttpParams(data)
//        //head
//        jsonHttpParams.putHeaders("Accept", "application/json")
//        jsonHttpParams.putHeaders("User-Agent", "WA/1.0.0 iPhone/9.1.1")
//        jsonHttpParams.putHeaders("Wa-Api-Id", WaApiId)
//        jsonHttpParams.putHeaders("Wa-Api-Key", WaApiKey)
//        jsonHttpParams.putHeaders("Wa-Api-Platform", "Android")
//        jsonHttpParams.putHeaders("Content-Type", "application/json; charset=utf-8")
//        jsonHttpParams.putHeaders("Wa-Client-ID", BoshSP.getPushObjectId())
//        //body
//        if (data != null) {
//            jsonHttpParams.putJsonParams(Gson().toJson(data))
//        }
//        if (useToken) {
//            jsonHttpParams.putHeaders("Authorization", "Bearer " + BoshSP.getAccessToken())
//        }
        return jsonHttpParams
    }

    protected fun get(baseUrl: String, data: Map<String, Any>?, httpCallback: HttpCallback, userCache: Boolean = false) {
        val cData = mutableMapOf<String, Any>()
        if (data != null) {
            cData.putAll(data)
        }
        val stringBuilder = StringBuilder()
        cData.forEach {
            stringBuilder.append(if (stringBuilder.isEmpty() && !baseUrl.contains("?")) "?" else "&")
            stringBuilder.append(it.key + "=" + it.value.toString())
        }
        val url = baseUrl + String(stringBuilder)
        fetch(url, RxVolley.Method.GET, getHttpParams(null), RxVolley.ContentType.JSON, httpCallback, userCache)
    }

    /**
     * @param contentType {@link #RxVolley.ContentType.JSON} or {@link #RxVolley.ContentType.FORM}
     */
    private fun fetch(url: String, httpMethod: Int, params: HttpParams, contentType: Int, callback: HttpCallback,
                      useCache: Boolean = false, listener: ProgressListener? = null) {
        var cUrl = url
        if (httpMethod != RxVolley.Method.GET) {
//            cUrl += "?locale=${getLocaleLanguage()}"
        }
        ArRxVolley.Builder().url(cUrl).params(params).httpMethod(httpMethod).contentType(contentType).callback(callback)
                .useServerControl(!useCache)
                .progressListener(listener)
                .doTask()
    }
}
