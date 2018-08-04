package hello.com.aramis.ndk.cmake.ui.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.aramis.library.extentions.logE
import fcom.aramisapp.base.AraBaseActivity
import fcom.aramisapp.base.AraBasePresenter
import hello.com.aramis.ndk.cmake.R
import hello.com.aramis.ndk.cmake.utils.AraFileSplitUtil
import kotlinx.android.synthetic.main.activity_file_split.*
import org.jetbrains.anko.toast
import java.io.File

/**
 *Created by Aramis
 *Date:2018/8/2
 *Description:
 */
class FileSplitActivity : AraBaseActivity() {
    private var isPermission = false
    private val fileNameOrigin = "liuyan.jpg"
//    private val fileNameOrigin = "www.txt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_split)

        setListener()
        logE("in in in")
        verifyStoragePermissions(this)
    }

    private fun verifyStoragePermissions(activity: Activity) {
        isPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        logE("文件权限：$isPermission")
        if (!isPermission) {
            logE("咋的回事 $isPermission")
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 1001)
        }//REQUEST_EXTERNAL_STRONGE是自定义个的一个对应码，用来验证请求是否通过
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        isPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    private fun setListener() {
        btn_file_split.setOnClickListener {
            if (isPermission) {
                val filePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + fileNameOrigin
                val file = File(filePath)
                if (file.exists()) {
                    toast("开始分割")
                    try {
                        AraFileSplitUtil.fileSplit(filePath, 3)
                    } catch (t: Throwable) {
                        toast("发生错误")
                    }

                } else {
                    toast("分割文件不存在")
                }
            } else {
                toast("权限限制，无法进行文件操作")
            }

        }
        btn_file_merge.setOnClickListener {
            if (isPermission) {
                val l = fileNameOrigin.split(".")
                val paths = arrayOf(Environment.getExternalStorageDirectory().absolutePath + File.separator + l[0] + "_0." + l[1],
                        Environment.getExternalStorageDirectory().absolutePath + File.separator + l[0] + "_1." + l[1],
                        Environment.getExternalStorageDirectory().absolutePath + File.separator + l[0] + "_2." + l[1])
                if (File(paths[0]).exists() && File(paths[1]).exists() && File(paths[2]).exists()) {
                    val newFileName = Environment.getExternalStorageDirectory().absolutePath + File.separator + l[0] + "_new." + l[1]
                    AraFileSplitUtil.fileMerge(newFileName, paths)
                } else {
                    toast("请先分割文件，再合并文件")
                }

            } else {
                toast("权限限制，无法进行文件操作")
            }
        }

        btn_file_copy.setOnClickListener {
            if (isPermission) {
                val filePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + fileNameOrigin
                if (File(filePath).exists()) {
                    val l = fileNameOrigin.split(".")
                    val copyFilePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + l[0] + "_copy." + l[1]
                    AraFileSplitUtil.fileCopy(filePath,copyFilePath)
                    toast("复制完成")
                } else {
                    toast("文件不存在")
                }
            } else {
                toast("权限限制，无法进行文件操作")
            }
        }

    }

    override fun getPresenter(): AraBasePresenter? = null
}