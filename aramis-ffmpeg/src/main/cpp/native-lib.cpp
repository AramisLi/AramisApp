#include <jni.h>
#include <string>
#include "dnffmpeg.h"
#include "android/native_window_jni.h"
#include "macro.h"


DNFFmpeg *dnfFmpeg = 0;
extern "C" {
#include "libavutil/avutil.h"
}

JavaVM *javaVM;
ANativeWindow *window = 0;
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;//静态初始化线程锁
JavaCallHelper *javaCallHelper=0;

int JNI_OnLoad(JavaVM *vm, void *r) {
    javaVM = vm;
    return JNI_VERSION_1_6;
}

//渲染。异步，需要加锁
void render(uint8_t *data, int linesize, int width, int height) {
    pthread_mutex_lock(&mutex);
    if (!window) {
        pthread_mutex_unlock(&mutex);
        return;
    }

    //设置窗口属性
    ANativeWindow_setBuffersGeometry(window, width, height,
                                     WINDOW_FORMAT_RGBA_8888);
    ANativeWindow_Buffer window_buffer;
    if (ANativeWindow_lock(window, &window_buffer, 0)) {//0=success,不是0就return
        ANativeWindow_release(window);
        window = 0;
        pthread_mutex_unlock(&mutex);
        return;
    }

    uint8_t *dst_data = static_cast<uint8_t *>(window_buffer.bits);
    //stride:一行有多少个数据（RGBA）*4
    int dst_linesize = window_buffer.stride * 4;
    //一行一行的拷贝
    for (int i = 0; i < window_buffer.height; ++i) {
        memcpy(dst_data + i * dst_linesize, data + i * linesize, static_cast<size_t>(dst_linesize));
    }
//    LOGE("正在渲染");
    ANativeWindow_unlockAndPost(window);
    pthread_mutex_unlock(&mutex);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_aramis_panda_DNPlayer_native_1prepare(JNIEnv *env, jobject instance, jstring dataSource_) {
    const char *dataSource = env->GetStringUTFChars(dataSource_, 0);
    javaCallHelper = new JavaCallHelper(javaVM, env, instance);
    //创建播放器
    dnfFmpeg = new DNFFmpeg(javaCallHelper, dataSource);
    dnfFmpeg->setRenderFrameCallBack(render);
    dnfFmpeg->prepare();
//    LOGE("prepare ing");
    env->ReleaseStringUTFChars(dataSource_, dataSource);
}

//开始播放
extern "C"
JNIEXPORT void JNICALL
Java_com_aramis_panda_DNPlayer_native_1start(JNIEnv *env, jobject instance) {
    dnfFmpeg->start();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_aramis_panda_DNPlayer_native_1setSurface(JNIEnv *env, jobject instance, jobject surface) {
    pthread_mutex_lock(&mutex);
    if (window) {
        ANativeWindow_release(window);
        window=0;
    }
    window = ANativeWindow_fromSurface(env, surface);
    pthread_mutex_unlock(&mutex);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_aramis_panda_DNPlayer_native_1stop(JNIEnv *env, jobject instance) {

    if (dnfFmpeg){
        LOGE("dnfFmpeg 来来来");
        dnfFmpeg->stop();
    }
    LOGE("stop 完成");
    DELETE(javaCallHelper);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_aramis_panda_DNPlayer_native_1release(JNIEnv *env, jobject instance) {

    pthread_mutex_lock(&mutex);
    if (window) {
        ANativeWindow_release(window);
        window=0;
    }
    pthread_mutex_unlock(&mutex);

}