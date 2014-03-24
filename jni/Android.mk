LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libjnidemo
LOCAL_SRC_FILES := native_method.cpp
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog  #若要打印log则必须包含
LOCAL_CFLAGS += -D_ARM_  #自定义宏
LOCAL_SHARED_LIBRARIES += libutils  #若要打印c++下的callstack则必须包含

include $(BUILD_SHARED_LIBRARY)
