package com.aramis.library.utils

import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.File

/**
 *Created by Aramis
 *Date:2018/8/24
 *Description:
 */
object AudioRecordManager {

    private var mediaRecorder: MediaRecorder? = null
    var onRecordStartListener: (() -> Unit)? = null
    var onRecordStopListener: (() -> Unit)? = null

    private var mediaPlayer: MediaPlayer? = null

    fun record(path: String) {
        record(File(path))
    }

    fun record(file: File) {
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        if (mediaRecorder != null) {
            stop()
        }

        mediaRecorder = MediaRecorder()
        mediaRecorder?.apply {
            this.setOutputFile(file.absolutePath)
            this.setAudioSource(MediaRecorder.AudioSource.MIC)
            this.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            this.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            this.prepare()
            this.start()
            onRecordStartListener?.invoke()
        }

    }

    fun stop() {
        mediaRecorder?.stop()
        release()
        onRecordStopListener?.invoke()
    }


    fun release() {
        mediaRecorder?.release()
        mediaRecorder = null
    }
}