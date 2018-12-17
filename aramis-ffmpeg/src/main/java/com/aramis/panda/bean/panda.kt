package com.aramis.panda.bean

import java.io.Serializable

/**
 *Created by Aramis
 *Date:2018/11/14
 *Description:
 */

data class PandaListBean(val items: List<PandaListItem>,val total:Int,val type:Type) : Serializable

data class PandaListItem(
        val id: String?, val name: String?, val hostid: String?, val person_num: String?,
        val style_type: Int?, val status: Int?, val fans: Int?,
        val userinfo: UserInfo?, val img: String?, val opt_type: String?, val url: String?, val click_trace: String?
) : Serializable

data class UserInfo(val rid: Int, val userName: String, val nickName: String, val avatar: String) : Serializable

data class Type(val cname:String,val ename:String):Serializable

data class PandaRoomBean(val info:RoomDataInfo):Serializable

data class RoomDataInfo(val hostinfo:HostInfo,val roominfo:RoomInfo,val videoinfo:VideoInfo):Serializable

data class HostInfo(val rid:String,val name:String,val avatar:String):Serializable

data class RoomInfo(val id:String,val name :String,val type:Int,val classification:String,
                    val cate:String,val bulletin:String,val pictures:Pictures):Serializable

data class VideoInfo(val room_key:String,val sign:String,val ts:String):Serializable

data class Pictures(val img:String):Serializable