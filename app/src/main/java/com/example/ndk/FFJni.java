package com.example.ndk;

/**
 * @author yudongliang
 * create time 2021-08-12
 * describe :
 */
public class FFJni {
    static {
        System.loadLibrary("main-ffmpeg");
    }

    public static String getFFVersion(){
        return nativeFFVersion();
    }
    private static native String nativeFFVersion();
}
