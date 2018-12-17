cmake_minimum_required(VERSION 3.4.1)

file(GLOB source_file src/main/cpp/*.cpp)

add_library(native-lib-face
        SHARED
        src/main/cpp/native-lib.cpp)

include_directories(src/main/cpp/include)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}  -L${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}")

#add_subdirectory(src/main/cpp/FaceAlignment)
#seeta_fa_lib

#include_directories(src/main/cpp/FaceAlignment/include)

target_link_libraries(native-lib-face
        opencv_java3
        log)




