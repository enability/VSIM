LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include /home/ennability/Downloads/opencv4android/OpenCV-2.4.10-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := jnicataract_lib 
LOCAL_SRC_FILES := jni_cataract.cpp
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)

