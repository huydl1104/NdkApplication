# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

set(jnilibs ${CMAKE_SOURCE_DIR}/../jniLibs)
set(libname main-ffmpeg)

include_directories(
        include
        glm
        common
        ${CMAKE_SOURCE_DIR}/recorder
        ${CMAKE_SOURCE_DIR}/util
        ${CMAKE_SOURCE_DIR}/player
        ${CMAKE_SOURCE_DIR}/player/decoder
        ${CMAKE_SOURCE_DIR}/player/render
        ${CMAKE_SOURCE_DIR}/player/render/audio
        ${CMAKE_SOURCE_DIR}/player/render/video
)

link_directories(
        ${jnilibs}/${ANDROID_ABI})

file(GLOB src-files
        ${CMAKE_SOURCE_DIR}/*.cpp
        ${CMAKE_SOURCE_DIR}/util/*.cpp
        ${CMAKE_SOURCE_DIR}/player/*.cpp
        ${CMAKE_SOURCE_DIR}/player/decoder/*.cpp
        ${CMAKE_SOURCE_DIR}/player/render/video/*.cpp
        ${CMAKE_SOURCE_DIR}/player/render/audio/*.cpp
        ${CMAKE_SOURCE_DIR}/recorder/SingleVideoRecorder.cpp
        ${CMAKE_SOURCE_DIR}/recorder/SingleAudioRecorder.cpp
        ${CMAKE_SOURCE_DIR}/recorder/MediaRecorderContext.cpp
        ${CMAKE_SOURCE_DIR}/recorder/GLCameraRender.cpp
        ${CMAKE_SOURCE_DIR}/recorder/MediaRecorder.cpp
        )

add_library( # Sets the name of the library.
        ${libname}

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${src-files}
        )

#add_library(avutil
#        SHARED
#        IMPORTED)
#set_target_properties(avutil
#        PROPERTIES IMPORTED_LOCATION
#        ${jnilibs}/${ANDROID_ABI}/libavutil.so)
#
#add_library(swresample
#        SHARED
#        IMPORTED)
#set_target_properties(swresample
#        PROPERTIES IMPORTED_LOCATION
#        ${jnilibs}/${ANDROID_ABI}/libswresample.so)
#
#add_library(avcodec
#        SHARED
#        IMPORTED)
#set_target_properties(avcodec
#        PROPERTIES IMPORTED_LOCATION
#        ${jnilibs}/${ANDROID_ABI}/libavcodec.so)
#
#add_library(avfilter
#        SHARED
#        IMPORTED)
#set_target_properties(avfilter
#        PROPERTIES IMPORTED_LOCATION
#        ${jnilibs}/${ANDROID_ABI}/libavfilter.so)
#
#add_library(swscale
#        SHARED
#        IMPORTED)
#set_target_properties(swscale
#        PROPERTIES IMPORTED_LOCATION
#        ${jnilibs}/${ANDROID_ABI}/libswscale.so)
#
#add_library(avformat
#        SHARED
#        IMPORTED)
#set_target_properties(avformat
#        PROPERTIES IMPORTED_LOCATION
#        ${jnilibs}/${ANDROID_ABI}/libavformat.so)

set(third-party-libs
        #文件格式和协议库
        avformat
        #编解码库，可以将第三方的codec以插件的形式添加进来
        avcodec
        #音视频的滤镜库，该模块包含了音频特效和视频特效的处理
        avfilter
        #用于音频重采样，可以对数字音频进行声道数、数据格式、采样率等多种基本信息的转换
        swresample
        #该模块用于图像格式转换，可以将 YUV 的数据转换为RGB的数据
        swscale
        #核心工具库，最基础模块之一，其他模块都会依赖该库做一些基本的音视频处理操作
        avutil
        fdk-aac
        x264
        )

set(native-libs
        android
        EGL
        GLESv3
        OpenSLES
        log
        m
        z
        )

target_link_libraries( # Specifies the target library.
        ${libname}

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        ${third-party-libs}
        ${native-libs}
        )