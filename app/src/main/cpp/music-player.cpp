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
#include "libswresample/swresample.h"
}

using namespace std;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_ndk_player_MusicPlayer_play(JNIEnv *env, jobject thiz,jstring url) {

}