cmake_minimum_required(VERSION 3.10.2)

# 支持gnu++11
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# 1. 定义so库和头文件所在目录，方面后面使用
set(ffmpeg_lib_dir ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})
set(ffmpeg_head_dir ${CMAKE_SOURCE_DIR})

# 2. 添加头文件目录
include_directories(${ffmpeg_head_dir}/include)

# 3. 添加ffmpeg相关的so库
add_library( avutil
        SHARED
        IMPORTED )
set_target_properties( avutil
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavutil-55.so )

add_library( swresample
        SHARED
        IMPORTED )
set_target_properties( swresample
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libswresample-2.so )

add_library( avcodec
        SHARED
        IMPORTED )
set_target_properties( avcodec
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavcodec-57.so )

add_library( avfilter
        SHARED
        IMPORTED)
set_target_properties( avfilter
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavfilter-6.so )

add_library( swscale
        SHARED
        IMPORTED)
set_target_properties( swscale
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libswscale-4.so )

add_library( avformat
        SHARED
        IMPORTED)
set_target_properties( avformat
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavformat-57.so )

add_library( avdevice
        SHARED
        IMPORTED)
set_target_properties( avdevice
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavdevice-57.so )

# 查找代码中使用到的系统库
find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )

# 配置目标so库编译信息
add_library( # Sets the name of the library.
        music-player
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        native-lib.cpp
        music-player.cpp
        )

# 指定编译目标库时，cmake要链接的库
target_link_libraries(
        # 指定目标库，native-lib 是在上面 add_library 中配置的目标库
        music-player
        # 4. 连接 FFmpeg 相关的库
        # 4.1、#核心工具库，最基础模块之一，其他模块都会依赖该库做一些基本的音视频处理操作
        avutil
        # 4.2、用于音频重采样，可以对数字音频进行声道数、数据格式、采样率等多种基本信息的转换
        swresample
        # 4.3、编解码库，可以将第三方的codec以插件的形式添加进来
        avcodec
        # 4.4、音视频的滤镜库，该模块包含了音频特效和视频特效的处理
        avfilter
        # 4.5、该模块用于图像格式转换，可以将 YUV 的数据转换为RGB的数据
        swscale
        # 4.6、文件格式和协议库
        avformat
        # 4.7、输入输出设备库
        avdevice

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )