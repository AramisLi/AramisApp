LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := hello-jni
LOCAL_SRC_FILES := com_ndkdev_utils_NDKUtilsJ.c
LOCAL_LDLIBS    := -lm -llog
include $(BUILD_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE := fmod
#LOCAL_SRC_FILES := $(LOCAL_PATH)/armeabi/libfmod.so
#LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/inc
#TARGET_ARCH_ABI := armeabi
#include $(PREBUILT_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE := fmodL
#LOCAL_SRC_FILES := $(LOCAL_PATH)/armeabi/libfmodL.so
#LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/inc
#TARGET_ARCH_ABI := armeabi
#include $(PREBUILT_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE    := qq_voicer
#LOCAL_SHARED_LIBRARIES := fmod fmodL
#LOCAL_SRC_FILES := effect_fix_c.c
#LOCAL_LDLIBS := -lm -llog
#LOCAL_CPP_FEATURES := exceptions
#TARGET_ARCH_ABI := armeabi
#include $(BUILD_SHARED_LIBRARY)
#common.cpp common_platform.cpp

