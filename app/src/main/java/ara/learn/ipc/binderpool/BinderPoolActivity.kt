package ara.learn.ipc.binderpool

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.support.v7.app.AppCompatActivity
import android.view.ViewConfiguration
import com.aramis.aramisapp.R
import com.aramis.library.extentions.logE
import kotlinx.android.synthetic.main.activity_filecache.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.downloadManager

/**
 *Created by Aramis
 *Date:2018/12/12
 *Description:
 */
class BinderPoolActivity : AppCompatActivity() {
    private val WHAT_SECURITY_ENCRYPT = 1
    private val WHAT_SECURITY_DECRYPT = 2
    private val WHAT_COMPUTE_ADD = 3

    private val content = "我是大帅哥"

    private var handler: Handler? = null
    private val pid = Process.myPid()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filecache)

        handler = Handler(Handler.Callback {
            logE("接收到返回值:"+it.what)
            val stringBuilder = StringBuilder(text_filecache.text)
            when (it.what) {
                WHAT_SECURITY_ENCRYPT -> stringBuilder.append("加密:").append(it.obj as String).append("\n")
                WHAT_SECURITY_DECRYPT -> stringBuilder.append("解密:").append(it.obj as String).append("\n")
                WHAT_COMPUTE_ADD -> stringBuilder.append("加法:").append(it.obj as String).append("\n")
            }

            text_filecache.text = stringBuilder.toString()
            false
        })

        btn_action.setOnClickListener {
            doAsync {
                doWork()
            }
        }

        text_filecache.text = "原始数据:$content pid:$pid\n"
        title="Binder连接池"

        val scaledTouchSlop = ViewConfiguration.get(this).scaledTouchSlop
    }

    private fun doWork() {
        val binderPool = BinderPool.getInstance(this)
        val securityCenter = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER) as SecurityCenterImpl

        logE(securityCenter.toString())
        val pass = securityCenter.encrypt(content)
        handler?.sendMessage(handler?.obtainMessage(WHAT_SECURITY_ENCRYPT, pass))
        val decrypt = securityCenter.decrypt(pass)
        handler?.sendMessage(handler?.obtainMessage(WHAT_SECURITY_DECRYPT, decrypt))
    }
}