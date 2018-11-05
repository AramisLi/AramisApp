package hello.com.aramis.opengl.douyin.record

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.opengl.EGLContext
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface

/**
 *Created by Aramis
 *Date:2018/11/5
 *Description:录制类
 */
class MediaRecorder(val context: Context, val path: String, val width: Int, val height: Int, val eglContext: EGLContext) {
    private var mMediaCodec: MediaCodec? = null

    private var mInputSurface: Surface? = null

    private var mMediaMuxer: MediaMuxer? = null

    private var mHandler: Handler? = null

    private var mEglBase: EGLBase? = null

    private var isStartRecord: Boolean = false

    private fun initMediaCodec() {
        //创建视频格式 AVC:高级编码H264
        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        //参数配置 1500K码率
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1500_000)
        //帧率
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20)
        //关键帧间隔
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 20)
        //颜色格式(RGB/YUV) 从surface中获取
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)

        //创建编码器
        mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mMediaCodec!!.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        //交给虚拟屏幕。通过opengl，将预览的纹理绘制到这个虚拟屏幕中，这样MediaCodec就会自动棉麻inputSurface
        mInputSurface = mMediaCodec!!.createInputSurface()


        //创建复用器(一个mpt的封装器，将h.264通过它写出道文件)
        mMediaMuxer = MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)


        //配置EGL环境
        val handlerThread = HandlerThread("VideoCodec")
        handlerThread.start()
        val looper = handlerThread.looper
        mHandler = Handler(looper)

        mHandler?.post {
            //子线程：EGL的绑定线程，对自己创建的EGL环境的openGL操作都在这个线程中执行
            //创建EGL环境。虚拟设备、EGL
            mEglBase = EGLBase(context, width, height, mInputSurface!!, eglContext)
            //启动编码器
            mMediaCodec?.start()
            isStartRecord = true
        }
    }

    /**
     * 床底纹理进来
     * 相当于调用一次就有一个新的图像需要编码
     */
    fun fireFrame(texture: Int, timestamp: Long) {
        if (!isStartRecord) {
            return
        }
        mHandler?.post {
            //把图像画到虚拟屏幕
            mEglBase?.draw(texture, timestamp)
            //从编码器的输出缓冲区获取编码后的数据
        }
    }

    private var index: Int = 0

    /**
     * 获取编码后的数据
     * @param endOfStream 标记是否结束录制
     */
    private fun getCodec(endOfStream: Boolean) {
        if (endOfStream) {
            //停止录制,给MediaCodec
            mMediaCodec?.signalEndOfInputStream()
        }

        //输出缓冲区
        val bufferInfo = MediaCodec.BufferInfo()
        loop@ while (true) {
            //等待10ms
            val status = mMediaCodec?.dequeueOutputBuffer(bufferInfo, 10_000) ?: 0
            //
            when (status) {
                //需要更多数据
                MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    if (!endOfStream) {
                        break@loop
                    }
                }
                //开始编码的时候，就会调用一次
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    val outputFormat = mMediaCodec?.outputFormat
                    //配置封装器.增加一路指定格式的媒体流 视频
                    index = mMediaMuxer?.addTrack(outputFormat) ?: 0

                }
                MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                    //忽略
                }
                else -> {
                    //成功取出一个有效的输出
                    val outputBuffer = mMediaCodec?.getOutputBuffer(status)
                    //如果获取的ByteBuffer是配置信息，不需要写出到mp4
                    if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        bufferInfo.size = 0
                    }

                    if (bufferInfo.size != 0) {
                        //写到mp4
                        outputBuffer?.apply {
                            //根据偏移定位
                            this.position(bufferInfo.offset)
                            //ByteBuffer 可读写的总长度
                            this.limit(bufferInfo.offset + bufferInfo.size)
                            //写出去
                            mMediaMuxer?.writeSampleData(index, outputBuffer, bufferInfo)
                        }
                    }
                    //输出缓冲区使用完了，可以回收了，让mediacodec继续使用
                    mMediaCodec?.releaseOutputBuffer(status, false)

                    //是否结束
                    if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        break@loop
                    }
                }
            }
        }
    }

    /**
     * 开始录制
     */
    fun start() {
        initMediaCodec()
        getCodec(false)
    }

    /**
     * 停止录制
     */
    fun stop() {
        isStartRecord = false
        mHandler?.post {
            getCodec(true)
            mMediaCodec?.stop()
            mMediaCodec?.release()
            mMediaCodec = null

            mMediaMuxer?.stop()
            mMediaMuxer?.release()
            mMediaMuxer = null

            mEglBase?.release()
            mEglBase = null

            mInputSurface = null
            mHandler?.looper?.quitSafely()
            mHandler = null

        }
    }
}