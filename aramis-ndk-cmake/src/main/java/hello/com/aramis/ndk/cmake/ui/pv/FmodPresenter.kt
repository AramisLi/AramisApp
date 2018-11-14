package hello.com.aramis.ndk.cmake.ui.pv

import android.media.MediaPlayer
import android.media.MediaRecorder
import com.aramis.library.base.Config
import fcom.aramisapp.base.AraBasePresenter
import fcom.aramisapp.base.AraBaseView
import java.io.File
import java.io.IOException


/**
 *Created by Aramis
 *Date:2018/8/23
 *Description:
 */
class FmodPresenter(view: FmodView) : AraBasePresenter<FmodView>(view) {
    private val mediaRecorder = MediaRecorder()
    private val mediaPlayer = MediaPlayer()
    private val audioFile = File(Config.baseFilePath + File.separator + "recording.3gp")
    private var isRecording = false

    fun getRecordFilePath(): String = audioFile.absolutePath

    fun androidRecordStart() {
        if (!isRecording) {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            val path = File(Config.baseFilePath)
            path.mkdirs()
            try {
                mediaRecorder.setOutputFile(audioFile.absolutePath)
                mediaRecorder.prepare()
                mediaRecorder.start()
                isRecording = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    fun androidRecordStop() {
        isRecording = false
        mediaRecorder.stop()
        mediaRecorder.release()
    }

    fun androidPlayAudio(path: String = getRecordFilePath()) {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.setDataSource(path)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }
    }
}

interface FmodView : AraBaseView {}
