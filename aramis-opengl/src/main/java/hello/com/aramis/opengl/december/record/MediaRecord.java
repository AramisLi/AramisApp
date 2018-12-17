package hello.com.aramis.opengl.december.record;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Aramis
 * Date:2018/12/6
 * Description: MediaCodec录制类
 */
public class MediaRecord {

    private final EGLContext mShareEglContext;
    private Context mContext;
    private String mPath;
    private int mWidth;
    private int mHeight;
    private MediaCodec mMediaCodec;
    private Surface mInputSurface;
    private MediaMuxer mMediaMuxer;

    private EGLBase mEglBase;
    private boolean isStart;
    private Handler mHandler;
    private int trackIndex;
    private float mSpeed;

    public MediaRecord(Context context, String path, int width, int height, EGLContext mShareEglContext) {
        mContext = context.getApplicationContext();
        mPath = path;
        mWidth = width;
        mHeight = height;
        this.mShareEglContext = mShareEglContext;
    }

    public void start(float speed) {
        this.mSpeed=speed;
        //视频格式
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight);
        //码率1500K
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1500_000);
        //帧率
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20);
        //关键帧间隔
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 20);
        //颜色格式(从surface中获取)
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            //编码器CONFIGURE_FLAG_ENCODE
            mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            //交给虚拟屏幕 通过opengl将预览的纹理绘制到这一个虚拟屏幕中
            //这样MediaCodec就会自动编码inputSurface中的图像
            mInputSurface = mMediaCodec.createInputSurface();

            //H.264-->MP4
            //复用器 一个mp4的封装器
            mMediaMuxer = new MediaMuxer(mPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            //配置EGL环境
            HandlerThread handlerThread = new HandlerThread("VideoCodec");
            handlerThread.start();
            Looper looper = handlerThread.getLooper();
            mHandler = new Handler(looper);
            //子线程 EGL的绑定线程
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //创建EGL环境
                    mEglBase = new EGLBase(mContext, mWidth, mHeight, mInputSurface, mShareEglContext);
                    //启动编码器
                    mMediaCodec.start();

                    isStart = true;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 传递纹理进来
     * 相当于调用一次就有一个新的
     */
    public void fireFrame(final int textureId, final long timestamp) {
        if (!isStart) {
            return;
        }

        //在子线程进行编码，不妨碍GLThread
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //把图像画到虚拟屏幕
                mEglBase.draw(textureId, timestamp);
                //从编码器的输出缓冲区中获取编码后的数据
                getCodec(false);
            }
        });

    }

    /**
     * 获取输出缓冲区的数据
     *
     * @param endOfStream 标记是否结束录制
     */
    private void getCodec(boolean endOfStream) {
        if (endOfStream) {
            //停止录制
            mMediaCodec.signalEndOfInputStream();
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (true) {
            //创建一个输出缓冲区

            //api21通过这个方法获取编码数据，之前使用getOutputBuffers
            //等待10毫秒
            int status = mMediaCodec.dequeueOutputBuffer(bufferInfo, 10_000);

            if (status == MediaCodec.INFO_TRY_AGAIN_LATER) {
                //需要更多数据
                if (!endOfStream) {
                    break;
                }
            } else if (status == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                //开始编码时，会调用一次
                MediaFormat outputFormat = mMediaCodec.getOutputFormat();
                //配置封装器
                //增加一路指定格式的媒体流(视频)
                trackIndex = mMediaMuxer.addTrack(outputFormat);
                mMediaMuxer.start();
            } else if (status == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                //忽略 ignore
            } else {
                //成功取出一个有效的输出数据
                ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(status);
                //如果获取的butebuffer 是配置信息，不需要写入mp4
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    bufferInfo.size = 0;
                }

                //不是配置信息
                if (bufferInfo.size != 0) {
                    bufferInfo.presentationTimeUs= (long) (bufferInfo.presentationTimeUs/mSpeed);
                    //写入mp4
                    //根据偏移来定位
                    outputBuffer.position(bufferInfo.offset);
                    //ByteBuffer 可读写总长度
                    outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                    //写到mp4
                    mMediaMuxer.writeSampleData(trackIndex, outputBuffer, bufferInfo);
                }

                //回收outputBuffer
                mMediaCodec.releaseOutputBuffer(status, false);

                //结束。如果调用mMediaCodec.signalEndOfInputStream(); 则bufferInfo.flags=MediaCodec.BUFFER_FLAG_END_OF_STREAM
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break;
                }
            }
        }

    }

    public void stop() {
        isStart = false;

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                getCodec(true);

                //释放资源
                mMediaCodec.stop();
                mMediaCodec.release();
                mMediaCodec = null;
                mMediaMuxer.stop();
                mMediaMuxer.release();
                mMediaMuxer = null;
                mEglBase.release();
                mEglBase = null;
                mInputSurface = null;
                mHandler.getLooper().quitSafely();
                mHandler = null;
            }
        });
    }

}

