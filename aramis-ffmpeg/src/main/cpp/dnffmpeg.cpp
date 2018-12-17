//
// Created by 李志丹 on 2018/11/13.
//

#include <cstring>
#include <pthread.h>
#include "dnffmpeg.h"
#include "macro.h"

extern "C" {
#include <libavformat/avformat.h>
#include <libavutil/time.h>
}


DNFFmpeg::DNFFmpeg(JavaCallHelper *callHelper, const char *dataSource) {
    this->callHelper = callHelper;
    //这里的dataSource是java层传过来的，在调用完成以后会释放掉，所以在这里不能直接等于，要重新拷贝过来
    //c++字符串是是以\0结尾，所以要+1
    this->dataSource = new char[strlen(dataSource) + 1];
    strcpy(this->dataSource, dataSource);

}

DNFFmpeg::~DNFFmpeg() {
    DELETE(dataSource);
}

void *task_prepare(void *args) {
    DNFFmpeg *ffmpeg = static_cast<DNFFmpeg *>(args);
    ffmpeg->_prepare();

    return 0;
}

void DNFFmpeg::prepare() {
    //创建一个线程
    pthread_create(&pid_prepare, 0, task_prepare, this);
}

void DNFFmpeg::_prepare() {
    //初始化网络,让ffmpeg能够使用网络
    avformat_network_init();
    //AVFormatContext包含了 视频的各种信息(宽、高等)
    avFormatContext = 0;
    AVDictionary *avDictionary = 0;
    av_dict_set(&avDictionary, "timeout", "5000000", 0);
    //1、打开媒体地址。给avFormatContext赋值,耗时操作
    int ret = avformat_open_input(&avFormatContext, dataSource, 0,&avDictionary);
    av_dict_free(&avDictionary);
    //ret=0表示成功，其他表示失败。文件路径错误或者无网络等，回调给java，给用户提示
    if (ret != 0 && callHelper) {
        LOGE("打开媒体失败:%s", av_err2str(ret));
        callHelper->onError(THREAD_CHILD, FFMPEG_CAN_NOT_OPEN_URL);
        return;
    }
    //2. 查找音视频中的 音视频流
    ret = avformat_find_stream_info(avFormatContext, 0);
    //ret>=0表示成功，其他表示失败
    if (ret < 0 && callHelper) {
        LOGE("查找流失败:%s", av_err2str(ret));
        callHelper->onError(THREAD_CHILD, FFMPEG_CAN_NOT_FIND_STREAM);
        return;
    }
    //3. 打开编解码器。avFormatContext->nb_streams 表示一个视频文件中的流的个数（视频流+音频流）
    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
        //可能是一个视频，也可能是一个视频
        AVStream *stream = avFormatContext->streams[i];
        //AVCodecParameters包含了这段流的各种参数信息(bit_rate码率,codecParameters->width宽，height高等)
        AVCodecParameters *codecParameters = stream->codecpar;
        //无论视频还是音频都需要干的一些事情(获得解码器等)
        //1.通过当前流使用的编码方式，查找解码器
        AVCodec *avCodec = avcodec_find_decoder(codecParameters->codec_id);
        if (avCodec == NULL && callHelper) {
            //ffmpeg不知道这个视频的解码
            callHelper->onError(THREAD_CHILD, FFMPEG_FIND_DECODER_FAIL);
            LOGE("查找解码器失败:%s", av_err2str(ret));
            return;
        }
        //2. 获得解码器上下文
        AVCodecContext *avCodecContext = avcodec_alloc_context3(avCodec);
        if (avCodecContext == NULL && callHelper) {
            LOGE("查找流失败:%s", av_err2str(ret));
            //ffmpeg不知道这个视频的解码
            callHelper->onError(THREAD_CHILD, FFMPEG_ALLOC_CODEC_COUTEXT_FAIL);
            return;
        }
        //3. 设置上下文内的一些参数
        ret = avcodec_parameters_to_context(avCodecContext, codecParameters);
        if (ret < 0 && callHelper) {//ret>=0表示成功，其他表示失败
            LOGE("查找失败:%s", av_err2str(ret));
            callHelper->onError(THREAD_CHILD, FFMPEG_CODEC_CONTEXT_PARAMTERS_FAIL);
            return;
        }
        //4. 打开解码器
        ret = avcodec_open2(avCodecContext, avCodec, 0);
        if (ret != 0 && callHelper) {
            callHelper->onError(THREAD_CHILD, FFMPEG_OPEN_DECODER_FAIL);
            return;
        }

        AVRational time_base = stream->time_base;

        //判断是视频还是音频
        if (codecParameters->codec_type == AVMEDIA_TYPE_AUDIO) {//音频
            audioChannel = new AudioChannel(i, avCodecContext, time_base);
        } else if (codecParameters->codec_type == AVMEDIA_TYPE_VIDEO) {//视频
            //帧率：单位时间内 需要显示多少个图像
            AVRational frame_rate = stream->avg_frame_rate;
            //计算fps 也可以使用函数av_q2d(frame_rate)
            int fps = frame_rate.num / frame_rate.den;

            videoChannel = new VideoChannel(i, avCodecContext, time_base, fps);
            videoChannel->setRenderFrameCallBack(renderFrameCallBack);
        }
    }

    if (!audioChannel && !videoChannel && callHelper) {//音视频都没有
        callHelper->onError(THREAD_CHILD, FFMPEG_NOMEDIA);
        return;
    }
    if (callHelper) {
        //准备完了。通知java，随时可以开始播放了
        callHelper->onPrepared(THREAD_CHILD);
    }
}

void *task_play(void *args) {
    DNFFmpeg *ffmpeg = static_cast<DNFFmpeg *>(args);
    ffmpeg->_start();
    return 0;
}

void DNFFmpeg::start() {
    isPlaying = 1;
    if (audioChannel) {
        audioChannel->play();
    }
    if (videoChannel) {
        videoChannel->setAudioChannel(audioChannel);
        videoChannel->play();
    }
    pthread_create(&pid_play, 0, task_play, this);
}

/**
 * 专门负责读取数据包
 */
void DNFFmpeg::_start() {
    //1. 读取媒体数据包
    int ret;
    while (isPlaying) {
        //避免读取的太快而导致的OOM
        if(audioChannel && audioChannel->packets.size()>100){
            av_usleep(10*1000);
            continue;
        }
        if (videoChannel && videoChannel->packets.size()>100){
            av_usleep(10*1000);
            continue;
        }

        AVPacket *avPacket = av_packet_alloc();
        ret = av_read_frame(avFormatContext, avPacket);
        //@return 0 if OK, < 0 on error or end of file
        if (ret == 0) {
            //成功
            //avPacket->stream_index
            if (audioChannel && avPacket->stream_index == audioChannel->id) {
                audioChannel->packets.push(avPacket);
            } else if (videoChannel && avPacket->stream_index == videoChannel->id) {
                videoChannel->packets.push(avPacket);
            }
        } else if (ret == AVERROR_EOF) {
            //文件读取完成
            if (audioChannel && audioChannel->frames.empty() && audioChannel->packets.empty() &&
                videoChannel && videoChannel->frames.empty() && videoChannel->packets.empty()) {
                break;
            }
        } else {
            //失败
            break;
        }
    }

    //播放完了
    isPlaying = 0;
    audioChannel->stop();
    videoChannel->stop();

}

void DNFFmpeg::setRenderFrameCallBack(RenderFrameCallBack renderFrameCallBack) {
    this->renderFrameCallBack = renderFrameCallBack;
}

void *aync_stop(void *args) {
    DNFFmpeg *ffmpeg = static_cast<DNFFmpeg *>(args);
    //等待prepare线程结束
    pthread_join(ffmpeg->pid_prepare, 0);
    //等待start线程结束(播放线程)
    pthread_join(ffmpeg->pid_play, 0);

    DELETE(ffmpeg->audioChannel);
    DELETE(ffmpeg->videoChannel);

    if (ffmpeg->videoChannel){
        LOGE("ffmpeg->videoChannel 没有被delete");
    }else{
        LOGE("ffmpeg->videoChannel deleted");
    }

    if (ffmpeg->avFormatContext) {
        avformat_close_input(&ffmpeg->avFormatContext);
        avformat_free_context(ffmpeg->avFormatContext);
        ffmpeg->avFormatContext = 0;
    }
    DELETE(ffmpeg);
    return 0;
}

void DNFFmpeg::stop() {
    isPlaying = 0;
//    DELETE(callHelper);
    pthread_create(&pid_stop, 0, aync_stop, this);
}


