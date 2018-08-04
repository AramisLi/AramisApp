//
// Created by 李志丹 on 2018/8/2.
//

#include <jni.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>
#include <android/log.h>

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"Aramis",FORMAT,__VA_ARGS__);

JNIEXPORT jstring JNICALL
Java_hello_com_aramis_ndk_cmake_utils_AraFileSplitUtil_helloJNI(JNIEnv *env, jclass type) {


    LOGE("我是大帅哥%d", 1);
    return (*env)->NewStringUTF(env, "我是大帅哥");
}

long getFileSize(char *file_path) {
    LOGE("getFileSize file_path:%s", file_path);
    FILE *f = fopen(file_path, "r");

    fseek(f, 0, SEEK_END);
    long size = ftell(f);
    LOGE("getFileSize :%ld", size);
    fclose(f);
    return size;
}

//文件分割
JNIEXPORT jint JNICALL
Java_hello_com_aramis_ndk_cmake_utils_AraFileSplitUtil_fileSplit(JNIEnv *env, jclass type,
                                                                 jstring filePath_, jint split) {
    const char *filePath = (*env)->GetStringUTFChars(env, filePath_, 0);
    char *a_filePath = malloc(sizeof(char) * strlen(filePath));
    strcpy(a_filePath, filePath);
    int length = (int) strlen(filePath);

    char *seq = ".";
    char *r = strtok(a_filePath, seq);
    char *file_name_sp[2];
    int file_name_count = 0;
    while (r != NULL) {
        if (file_name_count < 2) {
            file_name_sp[file_name_count] = r;
            file_name_count++;
        }
        r = strtok(NULL, seq);
    }

    FILE *fpr = fopen(filePath, "r");
    FILE *fpw = NULL;


    long fileSize = getFileSize((char *) filePath);
    long fileSize_per = fileSize / split;
    long fileSize_cur = 0;
    for (int i = 0; i < split; i++) {
        char *file_name = malloc(sizeof(char) * length + 10);
        char *b = malloc(sizeof(char) * 10);
        sprintf(b, "_%d.", i);
        strcpy(file_name, file_name_sp[0]);
        strcat(file_name, b);
        strcat(file_name, file_name_sp[1]);
        LOGE("分割的文件名称:%s\n", file_name);

        fpw = fopen(file_name, "w");
        int c = 0;
        int d = 1;

        if (i < split - 1) {
            while (fileSize_cur < fileSize_per * (i + 1)) {
                c = fgetc(fpr);
                fputc(c, fpw);
                fileSize_cur += sizeof(char);
            }
        } else {
            while ((c = fgetc(fpr)) != EOF) {
                fputc(c, fpw);
                fileSize_cur += sizeof(char);
            }
        }

        fclose(fpw);
        free(file_name);
        free(b);
    }


    fclose(fpr);
    (*env)->ReleaseStringUTFChars(env, filePath_, filePath);
    LOGE("%s\n", "分割完成");
    return 1;
}


//文件合并
JNIEXPORT void JNICALL
Java_hello_com_aramis_ndk_cmake_utils_AraFileSplitUtil_fileMerge(JNIEnv *env, jclass type,
                                                                 jstring newFileName_,
                                                                 jobjectArray paths) {
    const char *newFileName = (*env)->GetStringUTFChars(env, newFileName_, 0);
    FILE *fpw = fopen(newFileName, "wb");
    FILE *fpr = NULL;

    //获取数组长度
    int arr_length = (*env)->GetArrayLength(env, paths);
    for (int i = 0; i < arr_length; ++i) {
        //获取数组元素
        jstring s1 = (*env)->GetObjectArrayElement(env, paths, i);
        //转化为c字符串
        const char *s1_c = (*env)->GetStringUTFChars(env, s1, 0);
        fpr = fopen(s1_c, "rb");
        int c = 0;
        while ((c = fgetc(fpr)) != EOF) {
            fputc(c, fpw);
        }

        fclose(fpr);
        //释放字符串
        (*env)->ReleaseStringUTFChars(env, s1, s1_c);
    }

    fclose(fpw);

    (*env)->ReleaseStringUTFChars(env, newFileName_, newFileName);
    LOGE("%s", "合并完成");

}

JNIEXPORT void JNICALL
Java_hello_com_aramis_ndk_cmake_utils_AraFileSplitUtil_fileCopy(JNIEnv *env, jclass type,
                                                                jstring filePath_,
                                                                jstring copyFilePath_) {
    const char *filePath = (*env)->GetStringUTFChars(env, filePath_, 0);
    const char *copyFilePath = (*env)->GetStringUTFChars(env, copyFilePath_, 0);


    FILE *fpr = fopen(filePath, "r");
    FILE *fpw = fopen(copyFilePath, "w");

    char buffer[512];
    size_t len = 0;
    while ((len = fread(buffer, sizeof(char), 512, fpr)) != 0) {
        fwrite(buffer, sizeof(char), len, fpw);
    }

    fclose(fpr);
    fclose(fpw);
    (*env)->ReleaseStringUTFChars(env, filePath_, filePath);
    (*env)->ReleaseStringUTFChars(env, copyFilePath_, copyFilePath);
}