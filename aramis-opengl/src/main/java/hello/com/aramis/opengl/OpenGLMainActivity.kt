package hello.com.aramis.opengl

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.aramis.library.extentions.toDecimal
import hello.com.aramis.opengl.douyin.DouyinActivity
import hello.com.aramis.opengl.my.MyTestGLESActivity
import hello.com.aramis.opengl.douyin.utils.OpenGLHelper
import kotlinx.android.synthetic.main.activity_main_open.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class OpenGLMainActivity : AppCompatActivity() {
    private val soundIds = mutableListOf(R.raw.jianongpao, R.raw.jujiqiang, R.raw.lizi, R.raw.shanliang, R.raw.faguang)
    private val soundMap = mutableMapOf<Int, Int>()

    private var currentStreamId = 0
    private var position = 0
    private lateinit var soundPool: SoundPool
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_open)
//        setContentView(MySurfaceView2(this))

        initSoundPool()


        text_play_next.setOnClickListener {
            currentStreamId = soundPool.load(this, soundIds[position], 10)
        }

        text_play_dy.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions, 1001)
            } else {
                toDouyinActivity()
            }
        }

        text_play_my.setOnClickListener { startActivity<MyTestGLESActivity>() }

        if (OpenGLHelper.checkSupport20(this)) {
            val version = OpenGLHelper.getGLESVersion(this)
            toast("支持OpenGL ES 2.0。version:$version,toDecimal:${version.toString().toDecimal()}")
        } else {
            toast("不支持OpenGL ES 2.0 ${OpenGLHelper.getGLESVersion(this)}")
        }

    }

    private fun toDouyinActivity() {
        startActivity<DouyinActivity>()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var granted = true
        for (grantResult in grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                granted = false
                break
            }
        }
        if (granted) {
            toDouyinActivity()
        } else {
            toast("权限不足，无法完成操作")
        }
    }

    private fun initSoundPool() {
        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder().setMaxStreams(10).build()
        } else {
            SoundPool(10, AudioManager.STREAM_MUSIC, 1)
        }

        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
            soundPool.play(currentStreamId, 30f, 30f, 0, 0, 1f)
            position++
            if (position > soundIds.size - 1) {
                position = 0
            }
        }



    }
}
