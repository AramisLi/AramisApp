package hello.com.aramis.ndk.cmake.ui.activity

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import hello.com.aramis.ndk.cmake.R
import hello.com.aramis.ndk.cmake.ui.pv.FmodPresenter
import hello.com.aramis.ndk.cmake.ui.pv.FmodView
import hello.com.aramis.ndk.cmake.utils.FmodUtil
import kotlinx.android.synthetic.main.activity_fmod.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.io.File

/**
 *Created by Aramis
 *Date:2018/8/22
 *Description:
 */
class FmodActivity : AraBaseActivity(), FmodView {

    private var dialog: RecordDialog? = null
    private val presenter = FmodPresenter(this)
    private val yr = Environment.getExternalStorageDirectory().absolutePath + File.separator + "yiranyibaozha.mp3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fmod)
        initView()
        setListener()

        permission()


//        record_view.setOnLongClickListener {  }
    }

    private fun permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val perms = arrayOf("android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE")
            if (checkSelfPermission(perms[0]) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(perms[1]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(perms, 200)
            }
        }
    }

    private fun setListener() {
        text_fmod_record.setOnClickListener {
            //            toast(FmodUtil.test())
            presenter.androidRecordStart()
            dialog?.show()
        }
        text_fmod_play_android.setOnClickListener {

            doAsync {
                presenter.androidPlayAudio()
            }
        }

        text_fmod_play_fmod.setOnClickListener {
//            val path = yr
            val path = presenter.getRecordFilePath()
            if (File(path).exists()) {
                doAsync {
                    FmodUtil.play(path)
                }

            } else {
                toast("录音文件不存在")
            }
        }

    }

    private fun initView() {

        dialog = RecordDialog(this)
        dialog?.onAnimFinished = {
            toast("录音完成")
            presenter.androidRecordStop()
            text_fmod_file.text = presenter.getRecordFilePath()
            dialog?.dismiss()
        }

        if (File(presenter.getRecordFilePath()).exists()) {
            text_fmod_file.text = presenter.getRecordFilePath()
        }
    }

    override fun getPresenter(): AraBasePresenter? = presenter

    override fun onDestroy() {
        super.onDestroy()
//        FmodUtil.release()
    }
}