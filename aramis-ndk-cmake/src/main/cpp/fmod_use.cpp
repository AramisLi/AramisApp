//
// Created by 李志丹 on 2018/8/16.
//
#include <jni.h>
#include <string>
#include <libfmodL/fmod.hpp>
#include <unistd.h>

#define TYPE_LUOLI 1
#define TYPE_DASHU 2


extern "C" JNIEXPORT jstring
JNICALL
Java_hello_com_aramis_ndk_cmake_utils_FmodUtil_test(JNIEnv *env, jobject type) {

    std::string str = "test cpp success";
    return env->NewStringUTF(str.c_str());
}

extern "C" JNIEXPORT jstring
JNICALL
Java_hello_com_aramis_ndk_cmake_utils_FmodUtilJ_test(JNIEnv *env, jobject type) {

    std::string str = "test cpp success";
    return env->NewStringUTF(str.c_str());
}

extern "C" JNIEXPORT jint
JNICALL
Java_hello_com_aramis_ndk_cmake_utils_FmodUtil_play(JNIEnv *env, jobject type, jstring path_jstr) {
    const char *path_cstr = env->GetStringUTFChars(path_jstr, NULL);

    FMOD::System *fmodSystem;
    FMOD::Sound *sound;
    FMOD::Channel *channel;
    bool isPlaying = true;
    //初始化
    FMOD::System_Create(&fmodSystem);
    fmodSystem->init(32, FMOD_INIT_NORMAL, NULL);
    //创建声音
    fmodSystem->createSound(path_cstr, FMOD_DEFAULT, NULL, &sound);
    //播放声音
    fmodSystem->playSound(sound, 0, false, &channel);

    //update的时候才是真正的播放
    fmodSystem->update();

    while (isPlaying) {
        channel->isPlaying(&isPlaying);
        usleep(1 * 1000 * 1000);
    }

    //release
    env->ReleaseStringUTFChars(path_jstr, path_cstr);
    sound->release();
    fmodSystem->close();
    fmodSystem->release();
    return 1;
}

extern "C" JNIEXPORT void
JNICALL
Java_hello_com_aramis_ndk_cmake_utils_FmodUtil_playEffect(JNIEnv *env, jobject cls,
                                                          jstring path_jstr, jint type) {

    const char *path_cstr = env->GetStringUTFChars(path_jstr, NULL);

    FMOD::System *system;
    FMOD::Sound *sound;
    FMOD::Channel *channel;
    FMOD::DSP *dsp;
    bool isPlaying = true;

    FMOD::System_Create(&system);
    system->init(32, FMOD_INIT_NORMAL, NULL);
    system->createSound(path_cstr, FMOD_DEFAULT, NULL, &sound);

    switch (type) {
        case TYPE_LUOLI:
            //萝莉 提高音调
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            //设置音调的参数
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 2.5);

            system->playSound(sound, 0, false, &channel);
            //添加进音轨
            channel->addDSP(0, dsp);
            break;
        case TYPE_DASHU:
            //大叔 降低音调
            system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
            dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.8);
            system->playSound(sound, 0, false, &channel);
            channel->addDSP(0, dsp);
            break;
        default:
            break;
    }

    //update的时候才是真正的播放
    system->update();

    while (isPlaying) {
        channel->isPlaying(&isPlaying);
        usleep(1 * 1000 * 1000);
    }



    //release
    env->ReleaseStringUTFChars(path_jstr, path_cstr);
    sound->release();
    system->close();
    system->release();
}

extern "C" JNIEXPORT jboolean
JNICALL
Java_hello_com_aramis_ndk_cmake_utils_FmodUtil_isPlay(JNIEnv *env, jobject cls) {
    return 1;
}

extern "C" JNIEXPORT void
JNICALL
Java_hello_com_aramis_ndk_cmake_utils_FmodUtil_release(JNIEnv *env, jobject cls) {
//    fmodSystem->close();
//    fmodSystem->release();
}
