cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             ara-file-split

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/hello_com_aramis_ndk_cmake_utils_AraFileSplitUtil.c)

find_library( # Sets the name of the path variable.
           log-lib

           # Specifies the name of the NDK library that
           # you want CMake to locate.
           log )

target_link_libraries( # Specifies the target library.
                      ara-file-split

                      # Links the target library to the log library
                      # included in the NDK.
                      ${log-lib} )