#include <jni.h>
#include <stdio.h>
#include <stddef.h>
#include <stdlib.h>
#include <android/log.h>
#include <utils/CallStack.h>

//���ط�����ӡlog�ķ�ʽ
#define TAG "my-JNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL, TAG, __VA_ARGS__)

//û�д��������ֱ����java���ؾ�̬�ַ���
static jstring native_string(JNIEnv *env, jobject obj){
	LOGD("--> TEST JNI LOG!!!");
	return env->NewStringUTF("I'm from dynamic jni cpp file");
}

//java����string���͵Ĳ������������java���ؾ�̬�ַ���
static jstring native_set_to_jni(JNIEnv *env, jobject obj, jstring str){

	LOGD("--> native_set_to_jni");
//��ӡC++�Ķ�ջ��Ϣ�����Թ�����ֻ��ͨ��push���������������У�install��������
#ifdef _ARM_
	LOGD("--> native_set_to_jni callstack");
    android::CallStack stack;
    stack.update(1, 100);
    stack.dump("");
#endif
	if(str == NULL){
		LOGD("--> Params is empty");
		return env->NewStringUTF("params is empty");
	}

	const char *c_msg = NULL;
	c_msg = env->GetStringUTFChars(str, 0);
	LOGD("--> Get Params: %s from java", c_msg);

	return env->NewStringUTF("native_set_to_jni success");
}

//���ö�̬ӳ��ķ�ʽ
static JNINativeMethod methods[]={
		{"stringFromJNI", "()Ljava/lang/String;", (void *)native_string},
		{"setParamsToJNI", "(Ljava/lang/String;)Ljava/lang/String;", (void *)native_set_to_jni}
};

static const char *classPathName = "com/ckt/mydemo/NativeMethod";

jint JNI_OnLoad(JavaVM *vm, void *reserved){
	JNIEnv *env = NULL;
	jclass clazz;

	if(vm->GetEnv((void**)&env, JNI_VERSION_1_4) != JNI_OK){

	}

	clazz = env->FindClass(classPathName);
	if(clazz == NULL){
		return JNI_ERR;
	}

	if(env->RegisterNatives(clazz, methods, sizeof(methods)/sizeof(methods[0]))<0){
		return JNI_ERR;
	}
	return JNI_VERSION_1_4;
}
