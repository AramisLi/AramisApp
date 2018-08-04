package com.ndkdev

import android.os.Bundle
import com.ndkdev.ui.HelloJNIActivity
import com.ndkdev.ui.HelloJniActivityJ
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import fcom.aramisapp.component.SimpleTextAdapter
import kotlinx.android.synthetic.main.activity_ndk_show_list.*
import org.jetbrains.anko.startActivity

class NdkShowListActivity : AraBaseActivity() {
    private val dataList = mutableListOf("Hello JNI")
    private val adapter = SimpleTextAdapter(dataList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ndk_show_list)
        initView()
        setListener()
    }

    private fun setListener() {
        list_ndk_menu.setOnItemClickListener { adapterView, view, i, l ->
            when(i){
                0-> startActivity<HelloJniActivityJ>()
            }
        }
    }

    private fun initView() {
//        dataList.add("Hello JNI")
//        adapter.notifyDataSetChanged()
        list_ndk_menu.adapter = adapter

        "Supertypes of the following classes cannot be resolved. Please make sure you have the required dependencies in the classpath:\n" +
                "    class fcom.aramisapp.component.SimpleTextAdapter, unresolved supertypes: com.aramis.library.base.SimpleBaseAdapter\t"
    }

    override fun getPresenter(): AraBasePresenter? = null

}
