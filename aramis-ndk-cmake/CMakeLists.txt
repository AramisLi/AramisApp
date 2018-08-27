cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             ara-file-split

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/hello_com_aramis_ndk_cmake_utils_AraFileSplitUtil.c)

add_library(
            bsdiff
            SHARED
            src/main/cpp/hello_com_aramis_ndk_cmake_utils_FileBsdiffUtil.c
            src/main/cpp/bsdiff-4.3/bzlib.h
            src/main/cpp/bsdiff-4.3/bzlib_private.h
            src/main/cpp/bsdiff-4.3/bsdiff.c
            src/main/cpp/bsdiff-4.3/bspatch.c
            src/main/cpp/bsdiff-4.3/blocksort.c
            src/main/cpp/bsdiff-4.3/bzip2.c
            src/main/cpp/bsdiff-4.3/bzip2recover.c
            src/main/cpp/bsdiff-4.3/bzlib.c
            src/main/cpp/bsdiff-4.3/compress.c
            src/main/cpp/bsdiff-4.3/crctable.c
            src/main/cpp/bsdiff-4.3/decompress.c
            src/main/cpp/bsdiff-4.3/dlltest.c
            src/main/cpp/bsdiff-4.3/huffman.c
            src/main/cpp/bsdiff-4.3/mk251.c
            src/main/cpp/bsdiff-4.3/randtable.c
            src/main/cpp/bsdiff-4.3/spewG.c
            src/main/cpp/bsdiff-4.3/unzcrash.c
)

find_library( # Sets the name of the path variable.
           log-lib

           # Specifies the name of the NDK library that
           # you want CMake to locate.
           log )

target_link_libraries( # Specifies the target library.
                      ara-file-split
                      # Links the target library to the log library
                      # included in the NDK.
                      ${log-lib}  )

target_link_libraries( # Specifies the target library.
                      bsdiff

                      # Links the target library to the log library
                      # included in the NDK.
                      ${log-lib} )

add_library(fmod-use
            SHARED
            src/main/cpp/fmod_use.cpp)


add_library(fmod SHARED IMPORTED)

set_target_properties(fmod
PROPERTIES IMPORTED_LOCATION
${CMAKE_SOURCE_DIR}/src/main/cpp/${ANDROID_ABI}/libfmod.so)


add_library(fmodL SHARED IMPORTED)
set_target_properties(fmodL
PROPERTIES IMPORTED_LOCATION
${CMAKE_SOURCE_DIR}/src/main/cpp/${ANDROID_ABI}/libfmodL.so)

include_directories( src/main/cpp/include )
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")



#target_link_libraries(fmod-use ${log-lib} )
target_link_libraries(fmod-use fmod fmodL ${log-lib} )



