cmake_minimum_required(VERSION 3.4.1)

#file(GLOB source_file src/main/cpp/*.cpp)


#include_directories(src/main/cpp/include)

#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}  -L${CMAKE_SOURCE_DIR}/src/main/cpp/libs/${ANDROID_ABI}")

add_library(ara_native
        SHARED
        src/main/cpp/my_native.cpp)


target_link_libraries(ara_native log android)



