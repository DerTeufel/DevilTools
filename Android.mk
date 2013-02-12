# Copyright 2011 Crossbones Software

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4

LOCAL_SRC_FILES := $(call all-java-files-under,src)

LOCAL_PACKAGE_NAME := DevilTools

LOCAL_MODULE_TAGS := optional

# disable proguard to make easy debugging
#LOCAL_PROGUARD_FLAG_FILES := proguard.cfg

# sign apk with deviltools key
LOCAL_CERTIFICATE := deviltools

include $(BUILD_PACKAGE)
