//
// Created by qaz on 8/13/21.
//

#include "FFMediaPlayer.h"

void FFMediaPlayer::Init(JNIEnv *jniEnv, jobject obj, char *url, int renderType, jobject surface) {
    //jvm version
    jniEnv->GetJavaVM(&m_JavaVM);

}

