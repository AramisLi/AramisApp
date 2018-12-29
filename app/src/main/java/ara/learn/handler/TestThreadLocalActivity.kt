package ara.learn.handler

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import com.aramis.aramisapp.R
import com.aramis.library.extentions.logE
import kotlinx.android.synthetic.main.activity_anim_view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 *Created by Aramis
 *Date:2018/12/19
 *Description:
 */
class TestThreadLocalActivity : AppCompatActivity() {

    private val mBooleanThreadLocal = ThreadLocal<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_view)

        testThreadLocal()

        val nativeInit = MyMessageQueue().nativeInit()
        btn_1.text = nativeInit.toString()
    }

    /**
     * 不同线程访问同一个ThreadLocal变量，获得的值是不一样的。这是应为ThreadLocal在不同现成访问同一个
     * ThreadLocal的get方法时，ThreadLocal内部会从各自线程中取出一个数组，然后再从数组中根据当前ThreadLocal的
     * 索引去查找出对应的value值（ThreadLocalMap）
     */
    private fun testThreadLocal() {
        fun log(index: Int) {
            logE("{name:${Thread.currentThread().name} Thread#${if (index == 0) "main" else index.toString()}}mBooleanThreadLocal=${mBooleanThreadLocal.get()}")
        }
        mBooleanThreadLocal.set(true)
        log(0)

        Thread(Runnable {
            mBooleanThreadLocal.set(false)
            log(1)
            val myLooper = Looper.myLooper()
            Looper.prepare()
        }, "Thread#1").start()


        Thread(Runnable {
            log(2)
        }, "Thread#2").start()

        log(0)
    }


}