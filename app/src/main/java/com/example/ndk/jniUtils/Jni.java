package com.example.ndk.jniUtils;

/**
 * @author yudongliang
 * create time 2021-08-06
 * describe :
 */
public class Jni {

    static {
        System.loadLibrary("native-test");
    }

    public native void testStruct();
}
