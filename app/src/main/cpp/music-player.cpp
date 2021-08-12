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
#include "libavutil/avutil.h"
}
#define TAG "YDL"
using namespace std;


