//
// Created by 李志丹 on 2018/11/13.
//

#ifndef LANCELSN_VIDEOCHANNEL_H
#define LANCELSN_VIDEOCHANNEL_H

#include "BaseChannel.h"
#include "AudioChannel.h"

extern "C" {
#include <libswscale/swscale.h>
}

typedef void (*RenderFrameCallBack)(uint8_t *, int, int, int);

//解码视频
class VideoChannel : public BaseChannel {
public:
    VideoChannel(int id, AVCodecContext *avCodecContext,AVRational time_base,int fps);

    ~VideoChannel();

    void play();

    void decode();

    void render();

    void setRenderFrameCallBack(RenderFrameCallBack renderFrameCallBack1);

    void setAudioChannel(AudioChannel *audioChannel);

    void stop();

private:
    int fps;
    pthread_t pid_decode;
    pthread_t pid_render;
    SwsContext *swsContext;
    RenderFrameCallBack renderFrameCallBack;

    AudioChannel *audioChannel;
};


#endif //LANCELSN_VIDEOCHANNEL_H
