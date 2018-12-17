//
// Created by 李志丹 on 2018/11/13.
//

#include "JavaCallHelper.h"
#include "macro.h"

JavaCallHelper::JavaCallHelper(JavaVM *vm, JNIEnv *env, jobject instance) {
    this->vm = vm;
    //如果在主线程可以直接使用回调
    this->env = env;
    //一旦涉及到jobject 跨方法 跨线程 就需要创建全局引用
    this->instance = env->NewGlobalRef(instance);

    jclass clazz = env->GetObjectClass(instance);
    onErrorId = env->GetMethodID(clazz, "onError", "(I)V");
    onPreparedId = env->GetMethodID(clazz, "onPrepared", "()V");

}

JavaCallHelper::~JavaCallHelper() {
    env->DeleteGlobalRef(instance);
}

void JavaCallHelper::onError(int thread, int errorCode) {
    if (thread == THREAD_MAIN) {
        //如果是在主线程，直接回调
        env->CallVoidMethod(instance, onErrorId, errorCode);
    } else {
        //如果在子线程，需要先得到当前线程的env
        JNIEnv *env;
        vm->AttachCurrentThread(&env, 0);
        env->CallVoidMethod(instance, onErrorId, errorCode);
        vm->DetachCurrentThread();
    }
}

void JavaCallHelper::onPrepared(int thread){
    if (thread == THREAD_MAIN) {
        //如果是在主线程，直接回调
        env->CallVoidMethod(instance, onPreparedId);
    } else {
        //如果在子线程，需要先得到当前线程的env
        JNIEnv *env;
        vm->AttachCurrentThread(&env, 0);
        env->CallVoidMethod(instance, onPreparedId);
        vm->DetachCurrentThread();
    }
}
