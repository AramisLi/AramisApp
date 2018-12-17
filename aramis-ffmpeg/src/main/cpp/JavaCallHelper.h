//
// Created by 李志丹 on 2018/11/13.
//

#ifndef LANCELSN_JAVACALLHELPER_H
#define LANCELSN_JAVACALLHELPER_H


#include <jni.h>

class JavaCallHelper {
public:
    JavaCallHelper(JavaVM *vm,JNIEnv* env,jobject instance);
    ~JavaCallHelper();

    void onError(int thread,int errorCode);
    void onPrepared(int thread);

private:
    JavaVM *vm;
    JNIEnv* env;
    jobject instance;
    jmethodID onErrorId;
    jmethodID onPreparedId;
};


#endif //LANCELSN_JAVACALLHELPER_H
