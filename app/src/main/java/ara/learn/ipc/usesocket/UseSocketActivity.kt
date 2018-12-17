package ara.learn.ipc.usesocket

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.aramis.aramisapp.R
import com.aramis.library.base.SimpleBaseAdapter
import com.aramis.library.base.SimpleBaseAdapterHolder
import com.aramis.library.extentions.logE
import kotlinx.android.synthetic.main.activity_ipc_socket.*
import org.jetbrains.anko.doAsync
import java.io.*
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*

/**
 *Created by Aramis
 *Date:2018/12/12
 *Description:
 */
class UseSocketActivity : AppCompatActivity() {
    companion object {
        private val MESSAGE_RECEIVE_NEW_MSG = 1
        private val MESSAGE_SOCKEt_CONNECTED = 2
    }

    private var mClientSocket: Socket? = null

    private var mPrintWriter: PrintWriter? = null

    private var mHandler: Handler? = null

    private val chatList = mutableListOf<String>()
    private val adapter = ListViewAdapter(chatList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ipc_socket)

        logE("UseSocketActivity in in in")

        btn_action.isEnabled = false
        mHandler = Handler(Handler.Callback {
            when (it.what) {
                MESSAGE_SOCKEt_CONNECTED -> btn_action.isEnabled = true
                MESSAGE_RECEIVE_NEW_MSG -> {
                    chatList.add(it.obj as String)
                    adapter.notifyDataSetChanged()
                }
            }
            false
        })

        val intent = Intent(this, TCPServerService::class.java)
        startService(intent)

        btn_action.setOnClickListener {
            val msg = edit_action.text.toString()
            if (msg.isNotBlank()) {
                mPrintWriter?.println(msg)
                edit_action.setText("")
                val time = formatDateTime(System.currentTimeMillis())
                val showedmsg = "self $time:$msg\n"
                chatList.add(showedmsg)
                adapter.notifyDataSetChanged()
            }
        }

        list_chat.adapter = adapter

        doAsync {
            connectTCPServer()
        }
    }

    private fun connectTCPServer() {
        var socket: Socket? = null
        while (socket == null) {
            try {
                socket = Socket("localhost", 8688)
                mClientSocket = socket
                mPrintWriter = PrintWriter(BufferedWriter(OutputStreamWriter(mClientSocket?.getOutputStream())), true)
                mHandler?.sendEmptyMessage(MESSAGE_SOCKEt_CONNECTED)
                System.out.println("链接成功")
            } catch (e: IOException) {
                val t = 1000L
                System.out.println("链接失败,${t / 1000}秒后重试")
                SystemClock.sleep(t)
            }
        }

        val br = BufferedReader(InputStreamReader(socket.getInputStream()))
        while (!this.isFinishing && mClientSocket != null && !mClientSocket!!.isClosed) {
            val msg = br.readLine()
            if (msg != null) {
                val time = formatDateTime(System.currentTimeMillis())
                val showedMsg = "server $time:$msg\n"
                mHandler?.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, showedMsg)?.sendToTarget()
            }
        }

        br.close()
        mPrintWriter?.close()
        socket.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPrintWriter?.close()
        mClientSocket?.close()
    }

    private val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private fun formatDateTime(time: Long): String {
        return simpleDateFormat.format(Date(time))
    }

    private class ListViewAdapter(list: List<String>) : SimpleBaseAdapter<String>(list) {
        override fun initDatas(holder: SimpleBaseAdapterHolder, bean: String, position: Int) {
            (holder as ViewHolder).text_chat.text = bean
        }

        override fun itemLayout(): Int = R.layout.list_ipc_socket

        override fun initHolder(convertView: View): SimpleBaseAdapterHolder =
                ViewHolder(convertView.findViewById(R.id.text_chat))

        private inner class ViewHolder(val text_chat: TextView) : SimpleBaseAdapterHolder()

    }
}