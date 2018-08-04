LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := hello-jni
LOCAL_SRC_FILES := com_ndkdev_utils_NDKUtilsJ.c
LOCAL_LDLIBS    := -lm -llog
include $(BUILD_SHARED_LIBRARY)