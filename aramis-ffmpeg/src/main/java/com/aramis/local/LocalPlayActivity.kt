package com.aramis.local

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.aramis.library.base.SimpleBaseAdapter
import com.aramis.library.base.SimpleBaseAdapterHolder
import com.aramis.library.extentions.logE
import com.aramis.local.pv.LocalPlayPresenter
import com.aramis.local.pv.LocalPlayView
import com.aramis.panda.R
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import kotlinx.android.synthetic.main.activity_ffmpeg_local.*
import org.jetbrains.anko.toast
import java.io.File

/**
 *Created by Aramis
 *Date:2018/11/20
 *Description:
 */
class LocalPlayActivity : AraBaseActivity(), LocalPlayView {

    private val presenter = LocalPlayPresenter(this)
    private val dataList = mutableListOf<File>()
    private lateinit var adapter: ListViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg_local)

        adapter = ListViewAdapter(dataList)
        list_local_file.adapter = adapter

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            logE("没权限")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE), 102)
        } else {
            logE("有权限")
            presenter.getData(dataList)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            presenter.getData(dataList)
        }else{
            toast("需要文件读写权限")
        }
    }


    override fun onGetDataFinished() {
        logE("onGetDataFinished size:${dataList.size}")
        if (dataList.isEmpty()) {
            toast("没有发现可播放文件。请将可播放文件放在根目录下")
        } else {
            adapter.notifyDataSetChanged()
        }

    }


    private inner class ListViewAdapter(fileList: List<File>) : SimpleBaseAdapter<File>(fileList) {
        override fun initDatas(holder: SimpleBaseAdapterHolder, bean: File, position: Int) {
            (holder as ViewHolder).text_file_name.text = bean.name
        }

        override fun itemLayout(): Int = R.layout.list_ffmpeg_local

        override fun initHolder(convertView: View): SimpleBaseAdapterHolder {
            return ViewHolder(convertView.findViewById(R.id.image_file),
                    convertView.findViewById(R.id.text_file_name))
        }

        private inner class ViewHolder(val image_file: ImageView,
                                       val text_file_name: TextView) : SimpleBaseAdapterHolder()

    }


    override fun getPresenter(): AraBasePresenter<*>? = null
}