package hello.com.aramis.opengl.december

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.TextView
import hello.com.aramis.opengl.R
import hello.com.aramis.opengl.december.views.DouyinView
import kotlinx.android.synthetic.main.activity_december_main.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.toast

/**
 *Created by Aramis
 *Date:2018/12/5
 *Description:
 */
class DecemberOpenGLActivity : AppCompatActivity() {
    private val speedTexts = mutableListOf<TextView>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_december_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, 1001)
        } else {
//            cameraHelper.startPreview()
        }


        initView()

    }

    private fun initView() {
        douyinView.setOnClickListener {
            douyinView.autoFocus()
        }

        text_record.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    douyinView.startRecord()
                }
                MotionEvent.ACTION_UP -> {
                    douyinView.stopRecord()
                }

            }
            true
        }

        text_speed_0.setOnClickListener { onSpeedClick(0) }
        text_speed_1.setOnClickListener { onSpeedClick(1) }
        text_speed_2.setOnClickListener { onSpeedClick(2) }
        text_speed_3.setOnClickListener { onSpeedClick(3) }
        text_speed_4.setOnClickListener { onSpeedClick(4) }
        speedTexts.add(text_speed_0)
        speedTexts.add(text_speed_1)
        speedTexts.add(text_speed_2)
        speedTexts.add(text_speed_3)
        speedTexts.add(text_speed_4)
    }

    private fun onSpeedClick(position: Int) {
        speedTexts.forEach {
            it.background = null
        }
        speedTexts[position].backgroundResource = R.drawable.bg_dy_speed_white
        when (position) {
            0 -> douyinView.setSpeed(DouyinView.Speed.MODE_EXTRA_SLOW)
            1 -> douyinView.setSpeed(DouyinView.Speed.MODE_SLOW)
            2 -> douyinView.setSpeed(DouyinView.Speed.MODE_NORMAL)
            3 -> douyinView.setSpeed(DouyinView.Speed.MODE_FAST)
            4 -> douyinView.setSpeed(DouyinView.Speed.MODE_EXTRA_FAST)
        }
    }

}