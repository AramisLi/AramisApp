//
// Created by 李志丹 on 2018/11/13.
//

#ifndef LANCELSN_AUDIOCHANNEL_H
#define LANCELSN_AUDIOCHANNEL_H

#include "BaseChannel.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include "macro.h"

extern "C" {
#include <libswresample/swresample.h>
};

//解码音频
class AudioChannel : public BaseChannel {
public:
    AudioChannel(int id, AVCodecContext *avCodecContext,AVRational time_base);

    ~AudioChannel();

    void play();

    void decode();

    void _play();

    int getPcm();

    void stop();

    uint8_t *data = 0;

    int out_channels=0;//输出声道
    int out_samplesize=0;//输出采样位大小 16位或8位（2字节或1字节）
    int out_sample_rate=0;//输出的采样率
private:
    pthread_t pid_audio_decode;
    pthread_t pid_audio_play;

    SLObjectItf engineObject = 0;//引擎
    SLEngineItf engineInterface = 0;//引擎接口
    SLObjectItf outputMixObject = 0;//混音器
    SLObjectItf bqPlayerObject = 0;//播放器
    SLPlayItf bqPlayerInterface = 0;//播放器接口
    SLAndroidSimpleBufferQueueItf bqPlayerBufferQueueInterface = 0;

    //重采样
    SwrContext *swrContext = 0;


};


#endif //LANCELSN_AUDIOCHANNEL_H
