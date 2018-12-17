package ara.learn.ipc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ara.learn.ipc.binderpool.BinderPoolActivity
import ara.learn.ipc.filecache.FileCacheFirstActivity
import ara.learn.ipc.messenger.MessengerFirstActivity
import ara.learn.ipc.useaidl.UseAIDLActivity
import ara.learn.ipc.usecontentp.ProviderActivity
import ara.learn.ipc.usesocket.UseSocketActivity
import com.aramis.aramisapp.R
import fcom.aramisapp.component.SimpleTextAdapter
import kotlinx.android.synthetic.main.activity_list.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 *Created by Aramis
 *Date:2018/12/11
 *Description: ANDROID IPC实现方式
 */
class IPCListActivity : AppCompatActivity() {

    private val menuList = mutableListOf("1. Intent实现", "2. 共享文件实现", "3. Messenger实现", "4. AIDL实现", "5. ContentProvider实现", "6. Socket实现", "Binder连接池")
    private val adapter = SimpleTextAdapter(menuList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        list_default.adapter = adapter
        list_default.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> toast("Intent实现")
                1 -> startActivity<FileCacheFirstActivity>()
                2 -> startActivity<MessengerFirstActivity>()
                3 -> startActivity<UseAIDLActivity>()
                4 -> startActivity<ProviderActivity>()
                5 -> startActivity<UseSocketActivity>()
                6 -> startActivity<BinderPoolActivity>()
            }
        }

        title = "实现IPC的6种方式"
    }
}