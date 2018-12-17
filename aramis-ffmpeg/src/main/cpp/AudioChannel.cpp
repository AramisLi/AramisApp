//
// Created by 李志丹 on 2018/11/13.
//

#include "AudioChannel.h"

AudioChannel::AudioChannel(int aid, AVCodecContext *avCodecContext, AVRational time_base) : BaseChannel(aid,
                                                                                                        avCodecContext,
                                                                                                        time_base) {
    //双声道
    out_channels = av_get_channel_layout_nb_channels(AV_CH_LAYOUT_STEREO);
    //16位（2）
    out_samplesize = av_get_bytes_per_sample(AV_SAMPLE_FMT_S16);
    //采样率（通用44100Hz）
    out_sample_rate = 44100;
    //44100 * 2 * 2 。44100 * 2：1秒16位，8-》16，再*2 双声道
    size_t a = static_cast<size_t>(out_sample_rate * out_channels * out_samplesize);
    data = static_cast<uint8_t *>(malloc(a));
    memset(data, 0, static_cast<size_t>(out_sample_rate * out_channels * out_samplesize));
}

AudioChannel::~AudioChannel() {
    if (data) {
        free(data);
        data = 0;
    }
}

void *audio_decode(void *args) {
    AudioChannel *audioChannel = static_cast<AudioChannel *>(args);
    audioChannel->decode();
    return 0;
}

void *audio_play(void *args) {
    AudioChannel *audioChannel = static_cast<AudioChannel *>(args);
    audioChannel->_play();
    return 0;
}

void AudioChannel::play() {
    isPlaying = 1;
    packets.setWork(1);
    frames.setWork(1);
    swrContext = swr_alloc_set_opts(0, AV_CH_LAYOUT_STEREO, AV_SAMPLE_FMT_S16, out_sample_rate,
                                    avCodecContext->channel_layout,
                                    avCodecContext->sample_fmt,
                                    avCodecContext->sample_rate, 0, 0);
    //初始化swrContext
    swr_init(swrContext);
    //两个线程 1.解码 2.播放（OpenGL ES）
    pthread_create(&pid_audio_decode, 0, audio_decode, this);
    pthread_create(&pid_audio_play, 0, audio_play, this);
}

void AudioChannel::decode() {
    AVPacket *packet = 0;
    while (isPlaying) {
        int ret = packets.pop(packet);
        if (!isPlaying) {
            break;
        }
        //取出失败
        if (!ret) {
            continue;
        }
        //把包丢给解码器
        ret = avcodec_send_packet(avCodecContext, packet);
        releaseAvPacket(&packet);
        //重试
        if (ret != 0) {
            break;
        }

        //代表了一个图像
        AVFrame *frame = av_frame_alloc();
        ret = avcodec_receive_frame(avCodecContext, frame);
        if (ret == AVERROR(EAGAIN)) {
            continue;
        } else if (ret != 0) {
            break;
        }

        frames.push(frame);
    }
    releaseAvPacket(&packet);
}

//返回获取的pcm数据大小（字节）
int AudioChannel::getPcm() {
    int data_size = 0;
    AVFrame *frame;
    int ret = frames.pop(frame);
    if (!isPlaying) {
        if (ret) {
            releaseAvFrame(&frame);
        }
        return 0;
    }


    int64_t delays = swr_get_delay(swrContext, frame->sample_rate);
    //将nb_samples个数据 由sample_rate采样率转成44100后，返回有多少个数据
    //例如 10个44800 = nb 个 44100
    int64_t max_samples = av_rescale_rnd(delays + frame->nb_samples, out_sample_rate, frame->sample_rate, AV_ROUND_UP);

    //重采样 44800Hz 8位=》44100 16位
    //上下文+输出缓冲区+输出缓冲区能接受的最大数据量+输入数据+输入数据个数
    //返回每一个声道的输出数据
    int samples = swr_convert(swrContext, &data, static_cast<int>(max_samples),
                              const_cast<const uint8_t **>(frame->data),
                              frame->nb_samples);
    //获得字节大小(少*2的话 表示有多少个16位数据)
    data_size = samples * out_samplesize * out_channels;

    //获取frame的一个相对播放时间
    //获得相对播放这一段数据的秒数
    clock = frame->pts * av_q2d(time_base);
    releaseAvFrame(&frame);
    return data_size;
}

void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context) {
    AudioChannel *audioChannel = static_cast<AudioChannel *>(context);

    //获得pcm数据 多少个字节
    int data_size = audioChannel->getPcm();
    if (data_size > 0) {
        //接收16位数据
        (*bq)->Enqueue(bq, audioChannel->data, static_cast<SLuint32>(data_size));
    }
}

void AudioChannel::_play() {
    //1. 创建引擎并获取引擎接口
    SLresult result;
    //1.1 创建引擎
    result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    if (SL_RESULT_SUCCESS != result) {//引擎创建失败
        return;
    }
    //1.2 初始化引擎
    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {//引擎创建失败
        return;
    }
    //1.3 获取引擎接口SLEngineItf engineInterface
    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineInterface);
    if (SL_RESULT_SUCCESS != result) {//引擎创建失败
        return;
    }

    //2. 设置混音器
    //2.1 创建混音器SLObjectItf outputMixObject
    result = (*engineInterface)->CreateOutputMix(engineInterface, &outputMixObject, 0, 0, 0);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }
    //2.2 初始化混音器
    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }

    //3 创建播放器
    //3.1 配置输入声音信息(输入)
    //创建buffer缓冲类型的队列 2个队列
    SLDataLocator_AndroidBufferQueue android_queue = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
    //pcm
    //1:SL_DATAFORMAT_PCM
    //2: 2:两个声道
    //3：采样率：44100Hz 通用
    //4：采样位，SL_PCMSAMPLEFORMAT_FIXED_16：16位，SL_PCMSAMPLEFORMAT_FIXED_8：8位
    //5：数据容量：同上
    //6：LEFT+RIGHT 双声道
    //7: 小端数据
    SLDataFormat_PCM pcm = {SL_DATAFORMAT_PCM, 2, SL_SAMPLINGRATE_44_1,
                            SL_PCMSAMPLEFORMAT_FIXED_16, SL_PCMSAMPLEFORMAT_FIXED_16,
                            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,
                            SL_BYTEORDER_LITTLEENDIAN};

    //数据源 将上述配置信息放到这个数据源中
    SLDataSource slDataSource = {&android_queue, &pcm};

    //3.2 配置音轨(输出)
    SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSnk = {&outputMix, NULL};//音轨
    //需要的接口
    const SLInterfaceID ids[1] = {SL_IID_BUFFERQUEUE};
    const SLboolean req[1] = {SL_BOOLEAN_TRUE};
    //创建播放器
    (*engineInterface)->CreateAudioPlayer(engineInterface, &bqPlayerObject, &slDataSource,
                                          &audioSnk, 1, ids, req);
    //初始化播放器
    (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);

    //得到接口后调用 获取Player接口
    (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerInterface);

    //4. 设置播放回调页面
    //获取播放器队列接口
    (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE,
                                    &bqPlayerBufferQueueInterface);
    //设置回调
    (*bqPlayerBufferQueueInterface)->RegisterCallback(bqPlayerBufferQueueInterface, bqPlayerCallback, this);

    //5.设置播放状态
    (*bqPlayerInterface)->SetPlayState(bqPlayerInterface, SL_PLAYSTATE_PLAYING);

    //6. 手动激活
    bqPlayerCallback(bqPlayerBufferQueueInterface, this);

}

void AudioChannel::stop() {
    isPlaying = 0;
    packets.setWork(0);
    frames.setWork(0);
    pthread_join(pid_audio_decode, 0);
    pthread_join(pid_audio_play, 0);

    if (swrContext) {
        swr_free(&swrContext);
        swrContext = 0;
    }

    //释放播放器
    if (bqPlayerObject) {
        (*bqPlayerObject)->Destroy(bqPlayerObject);
        bqPlayerObject = 0;
        bqPlayerBufferQueueInterface = 0;
        bqPlayerInterface = 0;
    }
    //释放混音器
    if (outputMixObject) {
        (*outputMixObject)->Destroy(outputMixObject);
        outputMixObject = 0;
    }
    //释放引擎
    if (engineObject) {
        (*engineObject)->Destroy(engineObject);
        engineObject = 0;
        engineInterface = 0;
    }
}








