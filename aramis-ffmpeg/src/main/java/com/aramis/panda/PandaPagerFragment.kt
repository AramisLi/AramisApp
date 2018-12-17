package com.aramis.panda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.aramis.library.base.BaseFragment
import com.aramis.library.base.SimpleBaseAdapter
import com.aramis.library.base.SimpleBaseAdapterHolder
import com.aramis.library.extentions.logE
import com.aramis.panda.bean.PandaListBean
import com.aramis.panda.bean.PandaListItem
import com.aramis.panda.bean.PandaRoomBean
import com.aramis.panda.protocol.Protocol
import com.bumptech.glide.Glide
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast

/**
 *Created by Aramis
 *Date:2018/11/14
 *Description:
 */
abstract class PandaPagerFragment : BaseFragment() {
    private val dataList = mutableListOf<PandaListItem>()
    private var gridViewAdapter: GridViewAdapter? = null
    //避免暴力点击
    private var isAlreadyClick = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val listView = ListView(activity)
        gridViewAdapter = GridViewAdapter(dataList)
        listView.adapter = gridViewAdapter
        initData()
        listView.setOnItemClickListener { parent, view, position, id ->
            if (!isAlreadyClick) {
                isAlreadyClick = true
                dataList[position].id?.apply {
                    toRoomActivity(this)
                }
            } else {
                toast("努力获取中，请稍后")
            }

        }
        return listView
    }

    private fun toRoomActivity(roomId: String) {
        (activity as? PandaListActivity)?.apply {

            doGet<PandaRoomBean>(Protocol.getRoomDetail(roomId), {
                isAlreadyClick = false
                val liveUrl = Protocol.getLive(it.info.videoinfo.room_key, it.info.videoinfo.sign, it.info.videoinfo.ts)
                //http://pl3.live.panda.tv/live_panda/c8cf1b2658490d0add221512d673c242_mid.flv?sign=f00a5cb5894a4cfed48e3ec12ccd4b10&time=&ts=5bed4193&rid=-50992719
                //http://pl3.live.panda.tv/live_panda/c8cf1b2658490d0add221512d673c242_mid.flv?sign=5761f15d41102db1c4382d2463da9d46&time=1542275559&ts=5bed41e8&rid=-41465526

                logE("直播地址：$liveUrl")
                startActivity<PandaRoomActivity>(
                        "roomId" to roomId,
                        "liveUrl" to liveUrl
                )


            }, { errorNo, strMsg ->
                isAlreadyClick = false
                toast("请求错误 $errorNo$strMsg")
            })
        }
    }

    private fun initData() {
        val cate = when (getPosition()) {
            1 -> "yzdr"
            2 -> "food"
            else -> "lol"
        }
        (activity as PandaListActivity).doGet<PandaListBean>(Protocol.getListUrlByCate(cate), {
            onGetDataSuccess(it)
        }, { errorNo, strMsg ->
            onGetDataFail(errorNo, strMsg)
        })
    }

    private fun onGetDataSuccess(bean: PandaListBean) {
        logE("接受到数据 ${bean.items.size}")
        dataList.clear()
        dataList.addAll(bean.items.filterIndexed { index, _ -> index != 0 })
        gridViewAdapter?.notifyDataSetChanged()
    }

    private fun onGetDataFail(errorCode: Int, errorMessage: String?) {
        toast("网络请求错误 errorCode:$errorCode,errorMessage:$errorMessage")
    }

    private inner class GridViewAdapter(list: List<PandaListItem>) : SimpleBaseAdapter<PandaListItem>(list) {
        override fun initDatas(holder: SimpleBaseAdapterHolder, bean: PandaListItem, position: Int) {
            (holder as ViewHolder).text_room_name.text = bean.name
            Glide.with(activity!!).load(bean.img).into(holder.image_room_frame)
            bean.userinfo?.apply {
                Glide.with(activity!!).load(this.avatar).into(holder.image_zhubo_avatar)
                holder.text_zhubo_name.text = this.nickName
            }
        }

        override fun itemLayout(): Int = R.layout.frg_panda_grid

        override fun initHolder(convertView: View): SimpleBaseAdapterHolder =
                ViewHolder(
                        convertView.findViewById(R.id.image_room_frame),
                        convertView.findViewById(R.id.text_room_name),
                        convertView.findViewById(R.id.image_zhubo_avatar),
                        convertView.findViewById(R.id.text_zhubo_name)
                )

        private inner class ViewHolder(
                val image_room_frame: ImageView, val text_room_name: TextView,
                val image_zhubo_avatar: ImageView, val text_zhubo_name: TextView
        ) :
                SimpleBaseAdapterHolder()

    }

    abstract fun getPosition(): Int

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



