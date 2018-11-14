package fcom.aramisapp.base

import com.aramis.library.base.BaseActivity

/**
 *Created by Aramis
 *Date:2018/7/27
 *Description:
 */
abstract class AraBaseActivity :BaseActivity() {

    abstract override fun getPresenter(): AraBasePresenter<*>?
}