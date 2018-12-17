package com.aramis.panda.protocol

/**
 *Created by Aramis
 *Date:2018/11/14
 *Description:
 */
object Protocol {
    const val base = "https://api.m.panda.tv"
    //热门推荐
    val a = "$base/ajax_card_newlist?cate=index&__plat=android&__version=4.0.35.7878&__channel=yingyongbao"
    //所有分类
    val b = "$base/index.php?method=category.alllist&__plat=android&__version=4.0.35.7878&__channel=yingyongbao"

    //分类下列表
    fun getListUrlByCate(cate: String? = "lol"): String {
        return "$base/ajax_get_mobile4_live_list_by_cate?cate=$cate&needFilterMachine=1&pageno=1&pagenum=40&__plat=android&__version=4.0.35.7878&__channel=yingyongbao"
    }

    //房间详情
    fun getRoomDetail(roomId: String): String {
        return "$base/ajax_get_liveroom_baseinfo?roomid=$roomId&__version=4.0.35.7878&slaveflag=1&type=json&__plat=android"
    }

    //直播地址
    fun getLive(room_key: String, sign: String, ts: String): String {
        return "http://pl3.live.panda.tv/live_panda/${room_key}_mid.flv?sign=$sign&time=$ts"
    }

    private fun getTime(): String {
        return (System.currentTimeMillis() / 1000).toString()
    }
}