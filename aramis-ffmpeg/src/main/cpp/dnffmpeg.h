//
// Created by 李志丹 on 2018/11/13.
//

#ifndef LANCELSN_DNFFMPEG_H
#define LANCELSN_DNFFMPEG_H

#include "JavaCallHelper.h"
#include "AudioChannel.h"
#include "VideoChannel.h"

extern "C" {
#include <libavformat/avformat.h>
};

class DNFFmpeg {
public:
    DNFFmpeg(JavaCallHelper *callHelper, const char *dataSource);

    ~DNFFmpeg();

    void prepare();

    void _prepare();

    void start();

    void _start();

    void setRenderFrameCallBack(RenderFrameCallBack renderFrameCallBack);

    void stop();

    char *dataSource;
    pthread_t pid_prepare;
    pthread_t pid_stop;
    AVFormatContext *avFormatContext;
    JavaCallHelper *callHelper;
    AudioChannel *audioChannel = 0;
    VideoChannel *videoChannel = 0;
    bool isPlaying = 0;
    pthread_t pid_play;
    RenderFrameCallBack renderFrameCallBack;
};


#endif //LANCELSN_DNFFMPEG_H
