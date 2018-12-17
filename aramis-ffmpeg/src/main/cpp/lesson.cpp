//
// Created by 李志丹 on 2018/11/7.
//

#include <jni.h>
#include <iostream>
#include <string>
#include <android/log.h>

extern "C" {
extern int test();
}

#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, "JNI", __VA_ARGS__)

extern "C"
JNIEXPORT jstring JNICALL
Java_com_lancelsn_NativeUtils_test(JNIEnv *env, jobject type, jint a) {
    std::string returnValue = "我是大帅哥";
    __android_log_print(ANDROID_LOG_ERROR, "JNI", "为啥");
//    int t = test();
//    __android_log_print(ANDROID_LOG_ERROR, "JNI", "testcc%d", test());
    return env->NewStringUTF(returnValue.c_str());
}

struct AA {
    int a;
};

extern "C"
JNIEXPORT jstring JNICALL
Java_com_lancelsn_NativeUtils_test2(JNIEnv *env, jobject type, jstring a_, jintArray b_, jobjectArray c) {
    const char *a = env->GetStringUTFChars(a_, 0);
    jint *b = env->GetIntArrayElements(b_, NULL);


    env->ReleaseStringUTFChars(a_, a);
    env->ReleaseIntArrayElements(b_, b, 0);

//    env->NewStringUTF("");
    char *cc = const_cast<char *>(env->GetStringUTFChars(a_, NULL));

//    strcat(cc, "sdf");

    AA *a_a = new AA();

    delete a_a;


    jstring returnValue = env->NewStringUTF(cc);
    env->ReleaseStringUTFChars(a_, cc);

    return returnValue;
}

void aa() {

}

JavaVM *_vm;

//int JNI_OnLoad(JavaVM *vm, void *re) {
//    LOGE("JNI_OnLoad  in in in");
//    _vm = vm;
//
//    return JNI_VERSION_1_6;
//};

struct Context {
    jobject instance;
};


void *vvv(void *args) {
    JNIEnv *env = NULL;
    _vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);
    jint success = _vm->AttachCurrentThread(&env, 0);

    if (success != JNI_OK) {
        LOGE("env 初始化错误");
    } else {
        for (int i = 0; i < 10000000; ++i) {
            double a = i * i * i;
        }


//        Context *context = static_cast<Context *>(args);
//        jclass cls = env->GetObjectClass(context->instance);
        jclass cls = static_cast<jclass>(args);
        jmethodID id = env->GetMethodID(cls, "onCPPThreadFinished", "(I)V");
        if (id == NULL) {
            LOGE("获取jmethodID失败");
        } else {
            env->CallVoidMethod(cls, id);
        }

//        env->DeleteGlobalRef(context->instance);
//        delete context;
        _vm->DetachCurrentThread();
    }

    return 0;
}

void *threadTask(void *args) {

    JNIEnv *env;
    jint i = _vm->AttachCurrentThread(&env, 0);
    if (i == JNI_OK) {
        Context *context = static_cast<Context *>(args);
        jobject obj = static_cast<jclass>(context->instance);

        jclass cls = env->GetObjectClass(obj);
        jmethodID id = env->GetMethodID(cls, "onCPPThreadFinished", "(I)V");

        if (id == NULL) {
            LOGE("获取jmethodID失败");
        } else {
            LOGE("获取jmethodID成功");
        }

        env->CallVoidMethod(context->instance, id, 88);

        env->DeleteGlobalRef(context->instance);
        delete context;
    } else {
        LOGE("获取JNIEnv失败");

    }
    _vm->DetachCurrentThread();
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_lancelsn_NativeUtils_testCPPThread(JNIEnv *env, jobject object) {
    //启动c++线程
    pthread_t pid;
    Context *context = new Context;
    context->instance = env->NewGlobalRef(object);
    pthread_create(&pid, 0, threadTask, context);

    return 0;

}

JNIEXPORT jint JNICALL
Java_com_lancelsn_NativeUtils_testCPPThread2(JNIEnv *env, jobject instance, jobject thiz) {


    return 0;

}

