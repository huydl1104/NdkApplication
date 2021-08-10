//
// Created by qaz on 8/10/21.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include <stdlib.h>
#include <libavutil/ffversion.h>
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
    int ver_major,ver_minor,ver_micro;
    ver_major = (version>>16)&0xff;
    ver_minor = (version>>8)&0xff;
    ver_micro = (version)&0xff;
    //Current ffmpeg version is: 3.2.12 ,avcodec version is: 3752037=57.64.101
    __android_log_print(ANDROID_LOG_ERROR,TAG,"Current ffmpeg version is: %s ,avcodec version is: %d=%d.%d.%d\n",FFMPEG_VERSION,version,ver_major,ver_minor,ver_micro);
    std::string hello = "Hello from C++ ";
    return env->NewStringUTF(hello.c_str());
}