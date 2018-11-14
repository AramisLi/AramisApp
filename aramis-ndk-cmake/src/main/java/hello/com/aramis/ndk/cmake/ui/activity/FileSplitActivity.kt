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
import hello.com.aramis.ndk.cmake.utils.FileBsdiffUtil
import hello.com.aramis.ndk.cmake.utils.FmodUtilJ
import kotlinx.android.synthetic.main.activity_file_split.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.File

/**
 *Created by Aramis
 *Date:2018/8/2
 *Description:
 */
class FileSplitActivity : AraBaseActivity() {
    private var isPermission = false
    //    private val fileNameOrigin = "liuyan.jpg"
    //    private val fileNameOrigin = "www.txt"
    private val fileNameOrigin = "gaoqingtupian.jpg"

    private var isPatchRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_split)

        setListener()
        logE("in in in")
        verifyStoragePermissions(this)

        toast(FmodUtilJ.test())
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
            onPermissionGranted {
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
            }
        }
        btn_file_merge.setOnClickListener {
            onPermissionGranted {
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
            }
        }

        btn_file_copy.setOnClickListener {
            onPermissionGranted {
                val filePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + fileNameOrigin
                if (File(filePath).exists()) {
                    val l = fileNameOrigin.split(".")
                    val copyFilePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + l[0] + "_copy." + l[1]
                    AraFileSplitUtil.fileCopy(filePath, copyFilePath)
                    toast("复制完成")
                } else {
                    toast("文件不存在")
                }
            }
        }

        //文件差分
        btn_file_bsdiff.setOnClickListener {
            onPermissionGranted {
                val oldFilePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "gaoqingtupian_0.jpg"
                val newFilePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "gaoqingtupian.jpg"
                val patchFilePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "gaoqingtupian_patch"
                fileExists(oldFilePath, newFilePath) {
                    go({
                        val patchFile = File(patchFilePath)
                        if (patchFile.exists()) patchFile.delete()
                        FileBsdiffUtil.fileDiff(oldFilePath, newFilePath, patchFilePath)
                    }, {
                        toast("文件差分完成")
                    })
                }
            }
        }

        //文件合并
        btn_file_bspatch.setOnClickListener {
            val oldFilePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "gaoqingtupian_0.jpg"
            val newFilePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "gaoqingtupian_bspatch.jpg"
            val patchFilePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "gaoqingtupian_patch"
            fileExists(oldFilePath, patchFilePath) {
                go({
                    val newFile = File(newFilePath)
                    if (newFile.exists()) newFile.delete()
                    FileBsdiffUtil.filePatch(oldFilePath, newFilePath, patchFilePath)
                }, {
                    toast("文件差分合并完成")
                })

            }
        }

    }

    private fun go(async: () -> Unit, finish: () -> Unit) {
        doAsync {
            async.invoke()
            uiThread {
                finish.invoke()
            }
        }
    }

    private fun fileExists(vararg path: String, success: () -> Unit) {
        var b = true
        for (i in path) {
            if (!File(i).exists()) {
                b = false
                break
            }
        }

        if (b) {
            success.invoke()
        } else {
            toast("文件不存在")
        }
    }

    private fun onPermissionGranted(success: () -> Unit) {
        if (isPermission) {
            success.invoke()
        } else {
            toast("权限限制，无法进行文件操作")
        }
    }

    override fun getPresenter(): AraBasePresenter<*>? = null

}