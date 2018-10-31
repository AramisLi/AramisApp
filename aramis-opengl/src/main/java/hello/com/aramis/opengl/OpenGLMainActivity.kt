package hello.com.aramis.opengl

import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import hello.com.aramis.opengl.d2.MySurfaceView
import kotlinx.android.synthetic.main.activity_main_open.*

class OpenGLMainActivity : AppCompatActivity() {
    private val soundIds = mutableListOf(R.raw.jianongpao, R.raw.jujiqiang, R.raw.lizi, R.raw.shanliang, R.raw.faguang)
    private val soundMap = mutableMapOf<Int, Int>()

    private var currentStreamId = 0
    private var position = 0
    private lateinit var soundPool: SoundPool
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_open)


//        setContentView(MySurfaceView(this))
        initSoundPool()



//        text_play_next.setOnClickListener {
////            currentStreamId = soundPool.load(this, soundIds[position], 10)
////            soundMap[position]?.apply {
//            currentStreamId=soundPool.load(this,soundIds[position],10)
//
//
//
////            }
//        }


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
            if (position > soundIds.size-1) {
                position = 0
            }
        }

//        soundIds.forEach {
//            soundMap[it] = soundPool.load(this, it, 10)
//        }


    }
}
