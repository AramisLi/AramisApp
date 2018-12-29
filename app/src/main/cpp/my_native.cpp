//
// Created by 李志丹 on 2018/12/19.
//

#include <jni.h>
#include <string>


extern "C"
JNIEXPORT jlong JNICALL
Java_ara_learn_handler_MyMessageQueue_nativeInit(JNIEnv *env, jobject instance) {

//    NativeMessageQueue* nativeMessageQueue;
    jlong a = 100000;
    return a;
}
