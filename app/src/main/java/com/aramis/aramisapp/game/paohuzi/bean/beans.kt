package com.aramis.aramisapp.game.paohuzi.bean

import java.io.Serializable

/**
 *Created by Aramis
 *Date:2018/10/10
 *Description:
 */

data class Pai(val name: String, val value: Int, val index: Int, val isRed: Boolean = false, var canMoved: Boolean = true) : Serializable

data class Player(val name: String, val money: Long) : Serializable