package com.ckt.mydemo;

public class NativeMethod {
	public static native String stringFromJNI();
	public static native String setParamsToJNI(String str);
	static{
		System.loadLibrary("jnidemo");
	}
}
