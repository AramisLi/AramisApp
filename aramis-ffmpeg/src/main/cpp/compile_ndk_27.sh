#!/usr/bin/env bash

params_count=$#
if (test ${params_count} -lt 3)
then
    echo "参数有误"
else
    #第一个参数是NDK的路径
    NDK_DIR=$1
    #第二个参数是要编译的c/c++文件
    file_name=$2
    #第三个参数是编译后的文件名
    compile_file_name=$3

    if  test -d ${NDK_DIR};then
        if test -e ${file_name};then
        NDK_GCC=${NDK_DIR}/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/arm-linux-androideabi-gcc
        NDK_GCC_PARAMS="--sysroot=$NDK_DIR/platforms/android-27/arch-arm -isystem $NDK_DIR/sysroot/usr/include -isystem $NDK_DIR/sysroot/usr/include/arm-linux-androideabi -L$NDK_DIR/platforms/android-27/arch-arm/usr/lib -llog -landroid"

        ${NDK_GCC} ${NDK_GCC_PARAMS} -pie ${file_name} -o ${compile_file_name}
        else
        echo "文件不存在 file_name:$file_name"
        fi
    else
        echo "文件夹不存在 NDK_DIR:$NDK_DIR"
    fi
fi
