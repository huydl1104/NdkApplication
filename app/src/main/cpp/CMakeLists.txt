# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Declares and names the project.

project("ndk")
# 添加共享库搜索路径
LINK_DIRECTORIES(src/main/cpp/jniLibs/armeabi-v7a)
# 需要引入我们头文件,以这个配置的目录为基准
include_directories(src/main/cpp/jniLibs/armeabi-v7a/include)
# 指定源文件目录
AUX_SOURCE_DIRECTORY(src/main/cpp SRC_LIST)
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#配置so库信息
#1、生成so库的名字，此处为 libnative-lib.so
#2、STATIC：静态库，目标文件的归档文件，连接其他目标时候使用；SHARED：动态库会被动态连接，运行时被加载；MODEL：模块库不会被连接到其他目标中的插件
#3、资源文件可以是多个，资源的相对路径，相对于 CMakeLists.txt所在的目录
add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             native-lib.cpp )

#将第三方库作为动态库引用
add_library(avcodec-57
        SHARED
        IMPORTED)

#指定第三方库的绝对路径
set_target_properties(avcodec-57
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/cpp/jniLibs/armeabi-v7a/libavcodec-57.so)

#将第三方库作为动态库引用
add_library(avdevice-57
        SHARED
        IMPORTED)

#指定第三方库的绝对路径
set_target_properties(avdevice-57
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/cpp/jniLibs/armeabi-v7a/libavdevice-57.so)

#将第三方库作为动态库引用
add_library(avfilter-6
        SHARED
        IMPORTED)

#指定第三方库的绝对路径
set_target_properties(avfilter-6
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/cpp/jniLibs/armeabi-v7a/libavfilter-6.so)

#将第三方库作为动态库引用
add_library(avformat-57
        SHARED
        IMPORTED)

#指定第三方库的绝对路径
set_target_properties(avformat-57
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/cpp/jniLibs/armeabi-v7a/libavformat-57.so)

#将第三方库作为动态库引用
add_library(avutil-55
        SHARED
        IMPORTED)

#指定第三方库的绝对路径
set_target_properties(avutil-55
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/cpp/jniLibs/armeabi-v7a/libavutil-55.so)

#将第三方库作为动态库引用
add_library(postproc-54
        SHARED
        IMPORTED)

#指定第三方库的绝对路径
set_target_properties(postproc-54
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/cpp/jniLibs/armeabi-v7a/libpostproc-54.so)

#将第三方库作为动态库引用
add_library(swresample-2
        SHARED
        IMPORTED)

#指定第三方库的绝对路径
set_target_properties(swresample-2
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/cpp/jniLibs/armeabi-v7a/libswresample-2.so)

#将第三方库作为动态库引用
add_library(swscale-4
        SHARED
        IMPORTED)

#指定第三方库的绝对路径
set_target_properties(swscale-4
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/cpp/jniLibs/armeabi-v7a/libswscale-4.so)



# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.
#从系统查找依赖库
#1、android系统吧每个类型的库放在一个特定的位置。log库存放在log-lib中
#2、android系统在c环境下打log到logcat的库中
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
#配置库的依赖关系
#1、目标库 native-lib
#2、依赖于 ${log-lib}
#target_link_libraries( # Specifies the target library.
#                       native-lib
#
#                       # Links the target library to the log library
#                       # included in the NDK.
#                       ${log-lib} )

target_link_libraries(
        native-lib
        # 滤镜特效处理库
        ${avfilter-6}
        # 封装格式处理库
        ${avformat-57}
        # 编解码(最重要的库)
        ${avcodec-57}
        # 工具库(大部分库都需要这个库的支持)
        ${avutil-55}
        # 音频采样数据格式转换库
        ${swresample-2}
        # 视频像素数据格式转换
        ${swscale-4}
        # 设备信息
        ${avdevice-57}
        # 链接 android ndk 自带的一些库
        ${android}
        # 链接 OpenSLES
        ${OpenSLES}
        ${log-lib}
)