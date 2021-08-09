# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Declares and names the project.

project("ndk")

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
add_library(
        native-test
        SHARED
        native-test.cpp
)
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
target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
target_link_libraries(
        native-test
        ${log-lib}
)