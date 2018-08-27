//
// Created by 李志丹 on 2018/8/2.
//

#include <jni.h>
#include <string.h>
#include <android/log.h>
#include "bsdiff-4.3/bsdiff.h"

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"Aramis",FORMAT,__VA_ARGS__);

JNIEXPORT void JNICALL
Java_hello_com_aramis_ndk_cmake_utils_FileBsdiffUtil_fileDiff(JNIEnv *env, jobject type,
                                                              jstring oldFilePath_,
                                                              jstring newFilePath_,
                                                              jstring pathFilePath_) {
    const char *oldFilePath = (*env)->GetStringUTFChars(env, oldFilePath_, 0);
    const char *newFilePath = (*env)->GetStringUTFChars(env, newFilePath_, 0);
    const char *pathFilePath = (*env)->GetStringUTFChars(env, pathFilePath_, 0);

    char *argv[4];
    argv[0] = "AramisBsdiff";
    argv[1]= (char *) oldFilePath;
    argv[2]= (char *) newFilePath;
    argv[3]= (char *) pathFilePath;
    LOGE("oldFilePath:%s",argv[1]);
    LOGE("newFilePath:%s",argv[2]);
    LOGE("pathFilePath:%s",argv[3]);
    bsdiff_main(4, argv);
    (*env)->ReleaseStringUTFChars(env, oldFilePath_, oldFilePath);
    (*env)->ReleaseStringUTFChars(env, newFilePath_, newFilePath);
    (*env)->ReleaseStringUTFChars(env, pathFilePath_, pathFilePath);
}

JNIEXPORT void JNICALL
Java_hello_com_aramis_ndk_cmake_utils_FileBsdiffUtil_filePatch(JNIEnv *env, jobject type,
                                                               jstring oldFilePath_,
                                                               jstring newFilePath_,
                                                               jstring pathFilePath_) {
    const char *oldFilePath = (*env)->GetStringUTFChars(env, oldFilePath_, 0);
    const char *newFilePath = (*env)->GetStringUTFChars(env, newFilePath_, 0);
    const char *pathFilePath = (*env)->GetStringUTFChars(env, pathFilePath_, 0);

    char *argv[4];
    argv[0] = "AramisBsdiff";
    argv[1]= (char *) oldFilePath;
    argv[2]= (char *) newFilePath;
    argv[3]= (char *) pathFilePath;
    bspatch_main(4,argv);
    (*env)->ReleaseStringUTFChars(env, oldFilePath_, oldFilePath);
    (*env)->ReleaseStringUTFChars(env, newFilePath_, newFilePath);
    (*env)->ReleaseStringUTFChars(env, pathFilePath_, pathFilePath);
}