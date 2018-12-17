//
// Created by 李志丹 on 2018/11/13.
//

#ifndef LANCELSN_MACRO_H
#define LANCELSN_MACRO_H

#include <android/log.h>

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"FFMPEG",__VA_ARGS__)

//宏函数 安全delete对象
#define DELETE(obj) if(obj){ delete obj; obj=0; }

//标记线程是主线程还是子线程
#define THREAD_MAIN 1
#define THREAD_CHILD 2

//错误代码
#define FFMPEG_CAN_NOT_OPEN_URL 1 //打不开视频
#define FFMPEG_CAN_NOT_FIND_STREAM 2 //找不到流媒体
#define FFMPEG_FIND_DECODER_FAIL 3 //找不到解码器
#define FFMPEG_ALLOC_CODEC_COUTEXT_FAIL 4 //无法根据解码器创建上下文
#define FFMPEG_CODEC_CONTEXT_PARAMTERS_FAIL 6 //根据流信息，配置上下文参数失败
#define FFMPEG_OPEN_DECODER_FAIL 7 //打开解码器失败
#define FFMPEG_NOMEDIA 8 //没有音视频

#endif //LANCELSN_MACRO_H
