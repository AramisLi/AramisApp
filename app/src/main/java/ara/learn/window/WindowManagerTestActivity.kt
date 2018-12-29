package ara.learn.window

import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import com.aramis.aramisapp.R
import org.jetbrains.anko.toast

/**
 *Created by Aramis
 *Date:2018/12/18
 *Description:
 */
class WindowManagerTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_window_test)

        val button = Button(this)
        button.text = "大帅哥"
        val layoutParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, 0, 0, PixelFormat.TRANSPARENT)
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL and
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE and
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        layoutParams.gravity = Gravity.START and Gravity.TOP
        layoutParams.x = 0
        layoutParams.y = 0
        layoutParams.type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        windowManager.addView(button, layoutParams)

        button.setOnClickListener {
            toast("我是大帅哥")
        }

    }
}