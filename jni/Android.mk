LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := MAVKep
LOCAL_SRC_FILES := MAVKep.c
APP_CPPFLAGS += -fexceptions
APP_CFLAGS += -Os -fno-exceptions
CFLAGS += -Os -fno-exceptions
include $(BUILD_SHARED_LIBRARY)

