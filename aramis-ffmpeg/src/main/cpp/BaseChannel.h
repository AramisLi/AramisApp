//
// Created by 李志丹 on 2018/11/14.
//

#ifndef LANCELSN_BASECHANNEL_H
#define LANCELSN_BASECHANNEL_H


#include "safe_queue.h"
#include "macro.h"

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
}

class BaseChannel {
public:
    BaseChannel(int id, AVCodecContext *avCodecContext, AVRational time_base) : id(id),
                                                                                avCodecContext(
                                                                                        avCodecContext),
                                                                                time_base(
                                                                                        time_base) {
        packets.setReleaseCallBack(BaseChannel::releaseAvPacket);
        frames.setReleaseCallBack(BaseChannel::releaseAvFrame);
    }

    virtual ~BaseChannel() {
        packets.clear();
        frames.clear();

        if (avCodecContext) {
            avcodec_close(avCodecContext);
            avcodec_free_context(&avCodecContext);
            avCodecContext = 0;
        }

        LOGE("~BaseChannel 析构了");
    }

    static void releaseAvPacket(AVPacket **packet) {
        if (packet) {
            av_packet_free(packet);
            *packet = 0;
        }
    }

    static void releaseAvFrame(AVFrame **frame) {
        if (frame) {
            av_frame_free(frame);
            *frame = 0;
        }
    }

    //纯虚函数 相当于 抽象方法
    virtual void play()=0;

    virtual void stop()=0;

    int id;
    AVCodecContext *avCodecContext;
    //编码数据包队列
    SafeQueue<AVPacket *> packets;
    //解码数据包队列
    SafeQueue<AVFrame *> frames;
    int isPlaying;
    AVRational time_base;

    double clock;
};


#endif //LANCELSN_BASECHANNEL_H
