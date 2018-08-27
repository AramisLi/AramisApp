package hello.com.aramis.ndk.cmake.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Button
import com.aramis.library.extentions.logE

/**
 *Created by Aramis
 *Date:2018/8/24
 *Description:
 */
class RecordButton : Button {

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {


        this.setOnLongClickListener {
            logE("in in in")

            false
        }
    }

    //正在录制
    private val STATE_RECORDING = 1
    private val STATE_RECORD_UN = 2
    private var state = STATE_RECORD_UN

    private var record_prepare_time = 0L
    private var record_finish_time = 0L

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.action

        logE("onTouchEvent action:$action,x:${event?.x},y:${event?.y}")

        when (state) {
            STATE_RECORD_UN -> {
                if (action == MotionEvent.ACTION_DOWN) {

                }
            }

        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                state = STATE_RECORDING
                record_prepare_time = System.currentTimeMillis()
            }
            MotionEvent.ACTION_UP -> {
                state = STATE_RECORD_UN
                record_finish_time = System.currentTimeMillis()
            }
        }
//        return super.onTouchEvent(event)
        return true
    }

}