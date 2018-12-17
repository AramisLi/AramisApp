//
// Created by 李志丹 on 2018/11/13.
//


#include "VideoChannel.h"

extern "C" {
#include <libavutil/imgutils.h>
#include <libavutil/time.h>
}

void dropAvPacket(queue<AVPacket *> &q) {
    while (!q.empty()) {
        AVPacket *packet = q.front();
        //如果不属于I帧 就丢弃
        if (packet->flags != AV_PKT_FLAG_KEY) {
            BaseChannel::releaseAvPacket(&packet);
            q.pop();
        } else {
            break;
        }
    }
}

void dropAvFrame(queue<AVFrame *> &q) {
    if (!q.empty()) {
        AVFrame *avFrame = q.front();
        BaseChannel::releaseAvFrame(&avFrame);
        q.pop();
    }
}

VideoChannel::VideoChannel(int id, AVCodecContext *avCodecContext, AVRational time_base, int fps) : BaseChannel(id,
                                                                                                                avCodecContext,
                                                                                                                time_base) {
    this->fps = fps;
    frames.setSyncHandler(dropAvFrame);
}

VideoChannel::~VideoChannel() {
    frames.setReleaseCallBack(BaseChannel::releaseAvFrame);
    frames.clear();
}

void *decode_task(void *args) {
    VideoChannel *videoChannel = static_cast<VideoChannel *>(args);
    videoChannel->decode();
    return 0;
}

void *render_task(void *args) {
    VideoChannel *videoChannel = static_cast<VideoChannel *>(args);
    videoChannel->render();
    return 0;
}

void VideoChannel::play() {
    isPlaying = 1;
    packets.setWork(1);
    frames.setWork(1);
    pthread_create(&pid_decode, 0, decode_task, this);
    pthread_create(&pid_render, 0, render_task, this);
}

//解码
void VideoChannel::decode() {
    AVPacket *avPacket = 0;
    while (isPlaying) {
        int ret = packets.pop(avPacket);
        if (!isPlaying) {
            break;
        }
        if (!ret) {
            continue;
        }
        //把包丢给解码器
        ret = avcodec_send_packet(avCodecContext, avPacket);
        releaseAvPacket(&avPacket);
        if (ret != 0) {//0=success
            //解码失败
            break;
        }
        AVFrame *frame = av_frame_alloc();
        //从解码器中读取解码后的数据包
        ret = avcodec_receive_frame(avCodecContext, frame);
        if (ret == AVERROR(EAGAIN)) {//0=success
            //需要更多的数据
            continue;
        } else if (ret != 0) {
            break;
        }
        //已经得到了AVFrame,再开一个线程来播放，保证不影响解码
        frames.push(frame);

    }

    releaseAvPacket(&avPacket);
}


void VideoChannel::render() {
    swsContext = sws_getContext(avCodecContext->width, avCodecContext->height, avCodecContext->pix_fmt,
                                avCodecContext->width, avCodecContext->height, AV_PIX_FMT_RGBA, SWS_BILINEAR, NULL,
                                NULL, NULL);

    AVFrame *frame = 0;
    //指针数组
    uint8_t *dst_data[4];
    int dst_linesize[4];
    //申请内存
    av_image_alloc(dst_data, dst_linesize, avCodecContext->width, avCodecContext->height,
                   AV_PIX_FMT_RGBA, 1);


    double frame_delay = 1.0 / fps;
    while (isPlaying) {
        int ret = frames.pop(frame);
        if (!isPlaying) {
            break;
        }

        //src_linesize：表示每一行存放的字节长度
        sws_scale(swsContext, frame->data,
                  frame->linesize,
                  0,
                  avCodecContext->height, dst_data, dst_linesize);
        //将数据回调出去，进行播放
        if (renderFrameCallBack) {
            //和音频一样，获取当前这个画面播放的相对时间 best_effort_timestamp在大多数情况下是等于pts的，在视频相对时间上考虑了更多情况
            double clock = frame->best_effort_timestamp * av_q2d(time_base);
            //额外的间隔时间
            double extra_delay = frame->repeat_pict / (2 * fps);
            double delays = extra_delay + frame_delay;
            if (!audioChannel) {
                //休眠 单位：微秒
                av_usleep(static_cast<unsigned int>(delays * 1000 * 1000));
            } else {
                if (clock == 0) {
                    av_usleep(static_cast<unsigned int>(delays * 1000 * 1000));
                } else {
                    double audioClock = audioChannel->clock;
                    double diff = clock - audioClock;
                    if (diff > 0) {
//                        LOGE("视频快了 %lf",diff);
                        //大于0 表示视频比较快
                        av_usleep(static_cast<unsigned int>((delays + diff) * 1000 * 1000));
                    } else if (diff < 0) {
                        //小于0 表示音频比较快
//                        LOGE("音频快了 %lf",diff);
                        if (fabs(diff) >= 0.05) {
                            releaseAvFrame(&frame);
                            frames.sync();
                            continue;
                        } else {
                            //不睡了 快点赶上音频
                        }
                    }
                }
            }

            renderFrameCallBack(dst_data[0], dst_linesize[0], avCodecContext->width, avCodecContext->height);
        }
        releaseAvFrame(&frame);
    }
    av_freep(&dst_data[0]);
    releaseAvFrame(&frame);
    isPlaying=0;
    sws_freeContext(swsContext);
    swsContext=0;
}

void VideoChannel::setRenderFrameCallBack(RenderFrameCallBack renderFrameCallBack1) {
    this->renderFrameCallBack = renderFrameCallBack1;
}

void VideoChannel::setAudioChannel(AudioChannel *audioChannel) {
    this->audioChannel = audioChannel;
}

void VideoChannel::stop() {
    isPlaying=0;
    packets.setWork(0);
    frames.setWork(0);

    pthread_join(pid_decode,0);
    pthread_join(pid_render,0);

}





