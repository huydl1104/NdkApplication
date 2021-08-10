//
// Created by qaz on 8/10/21.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include <stdlib.h>
// 在 c++ 中采用 c 的这种编译方式
extern "C" {
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include "libswresample/swresample.h"
}
#define TAG "YDL"
using namespace std;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_ndk_player_MusicPlayer_play(JNIEnv *env, jobject thiz,jstring url) {
    unsigned int version = avcodec_version();
    __android_log_print(ANDROID_LOG_ERROR,TAG,"version %d ",version);
    std::string hello = "Hello from C++ ";
    return env->NewStringUTF(hello.c_str());
}