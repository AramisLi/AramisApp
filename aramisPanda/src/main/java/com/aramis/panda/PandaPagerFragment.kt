package com.aramis.panda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import com.aramis.library.base.SimpleBaseAdapter
import com.aramis.library.base.SimpleBaseAdapterHolder
import com.aramis.library.extentions.logE
import com.aramis.panda.bean.PandaListBean
import fcom.aramisapp.base.AraBaseFragment
import fcom.aramisapp.base.AraBasePresenter
import fcom.aramisapp.base.AraBaseView
import org.jetbrains.anko.support.v4.toast

/**
 *Created by Aramis
 *Date:2018/11/14
 *Description:
 */
abstract class PandaPagerFragment : AraBaseFragment(), PandaPagerFragmentView {
    private val presenter = PandaPagerFragmentPresenter(this)
    private val dataList = mutableListOf<PandaListBean>()
    private var gridViewAdapter: GridViewAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = GridView(activity)
        gridViewAdapter = GridViewAdapter(dataList)
        initData()
        return mRootView
    }

    private fun initData() {
        presenter.getDataList(getPosition())
    }

    override fun onGetDataSuccess(list: List<PandaListBean>) {
        dataList.clear()
        dataList.addAll(list)
        gridViewAdapter?.notifyDataSetChanged()
    }

    override fun onGetDataFail(errorCode: Int, errorMessage: String?) {
        toast("网络请求错误 errorCode:$errorCode,errorMessage:$errorMessage")
    }

    private inner class GridViewAdapter(list: List<PandaListBean>) : SimpleBaseAdapter<PandaListBean>(list) {
        override fun initDatas(holder: SimpleBaseAdapterHolder, bean: PandaListBean, position: Int) {
        }

        override fun itemLayout(): Int = R.layout.frg_panda_grid

        override fun initHolder(convertView: View): SimpleBaseAdapterHolder =
                ViewHolder(convertView.findViewById(R.id.image_room_frame),
                        convertView.findViewById(R.id.text_room_name))

        private inner class ViewHolder(val image_room_frame: ImageView, val text_room_name: TextView) : SimpleBaseAdapterHolder()

    }

    abstract fun getPosition(): Int

    override fun onDestroy() {
        super.onDestroy()
        presenter.dispatchView()
    }
}

class PandaPagerFragment1 : PandaPagerFragment() {
    override fun getPosition(): Int = 0
}

class PandaPagerFragment2 : PandaPagerFragment() {
    override fun getPosition(): Int = 1
}

class PandaPagerFragment3 : PandaPagerFragment() {
    override fun getPosition(): Int = 2
}

class PandaPagerFragmentPresenter(view: PandaPagerFragmentView) : AraBasePresenter<PandaPagerFragmentView>(view) {
    fun getDataList(index: Int) {
        val url = getUrl(index)
        get(url, null, getHttpCallBack({
            //            mView?.onGetDataSuccess()
            logE("获取到数据 $it")
        }, { errorCode, errorMsg ->
            mView?.onGetDataFail(errorCode, errorMsg)
        }))

    }

    private fun getUrl(index: Int): String {
        val ba = "https://api.m.panda.tv/"
        return "https://api.m.panda.tv/ajax_get_mobile4_live_list_by_cate?needFilterMachine=0&pageno=1&pagenum=40&__plat=android&__version=4.0.35.7878&__channel=yingyongbao"
//        return when (index) {
//            1 -> ba + "ajax_get_mobile4_live_list_by_cate?needFilterMachine=0&pageno=1&pagenum=40&__plat=android&__version=4.0.35.7878&__channel=yingyongbao"
//            2 -> ""
//            else -> ""
//        }
    }
}

interface PandaPagerFragmentView : AraBaseView {

    fun onGetDataSuccess(list: List<PandaListBean>)

    fun onGetDataFail(errorCode: Int, errorMessage: String?)

    override fun onNetError(errorCode: Int, errorMessage: String?) {

    }
}


