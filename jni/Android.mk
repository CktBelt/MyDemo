LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libjnidemo
LOCAL_SRC_FILES := native_method.cpp
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog  #��Ҫ��ӡlog��������
LOCAL_CFLAGS += -D_ARM_  #�Զ����
LOCAL_SHARED_LIBRARIES += libutils  #��Ҫ��ӡc++�µ�callstack��������

include $(BUILD_SHARED_LIBRARY)
