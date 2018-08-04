//
// Created by 李志丹 on 2018/7/27.
//
#include <jni.h>
#include <stdio.h>
#include <string.h>

JNIEXPORT jstring JNICALL
Java_com_ndkdev_utils_NDKUtilsJ_getStringFromC(JNIEnv *env, jclass type) {
    printf("=================================我是大帅哥");
//    jclass jcl = (*env)->GetObjectClass(env, type);
//    jfieldID iid = (*env)->GetFieldID(env, jcl, "paramInt", "I");
//    int a = (*env)->GetIntField(env, jcl, iid);
    int a = 5;
    printf("c获取到的java的int类型参数：%d", a);

    return (*env)->NewStringUTF(env, "Hello from C");
}

JNIEXPORT jstring JNICALL
Java_com_ndkdev_utils_NDKUtilsJ_getChineseString(JNIEnv *env, jclass type, jstring chinese_) {
    const char *chinese = (*env)->GetStringUTFChars(env, chinese_, 0);


    (*env)->ReleaseStringUTFChars(env, chinese_, chinese);

    //获取java String的class
    jclass jStringCls = (*env)->FindClass(env, "java/lang/String");
    //获取String的构造函数 String(byte[] bytes,String charsetName)
    jmethodID constructor_mid = (*env)->GetMethodID(env, jStringCls, "<init>",
                                                    "([BLjava/lang/String;)V");

    char *returnValue = "我是大帅哥aramis";
    jbyteArray bytes = (*env)->NewByteArray(env, (int) strlen(returnValue));
    //将returnValue中的数据赋值给byte数组
    (*env)->SetByteArrayRegion(env, bytes, 0, (int) strlen(returnValue), returnValue);

    jstring charsetName = (*env)->NewStringUTF(env, "UTF-8");
    jstring result = (*env)->NewObject(env, jStringCls, constructor_mid, bytes, charsetName);

    return (*env)->NewString(env, "sdkfjsdkfj", 11);
//    return result;
}

jstring getUTF8JString(JNIEnv *env, char *parseValue) {
//获取java String的class
    jclass jStringCls = (*env)->FindClass(env, "java/lang/String");
    //获取String的构造函数 String(byte[] bytes,String charsetName)
    jmethodID constructor_mid = (*env)->GetMethodID(env, jStringCls, "<init>",
                                                    "([BLjava/lang/String;)V");

    jbyteArray bytes = (*env)->NewByteArray(env, (int) strlen(parseValue));
    //将returnValue中的数据赋值给byte数组
    (*env)->SetByteArrayRegion(env, bytes, 0, (int) strlen(parseValue), (const jbyte *) parseValue);

    jstring charsetName = (*env)->NewStringUTF(env, "UTF-8");
    jobject result = (*env)->NewObject(env, jStringCls, constructor_mid, bytes, charsetName);
    return result;
}

JNIEXPORT jstring JNICALL
Java_com_ndkdev_utils_NDKUtilsJ_crypt(JNIEnv *env, jclass type, jstring str_) {
    const char *str = (char *) (*env)->GetStringUTFChars(env, str_, JNI_FALSE);

    int l = (int) strlen(str);
    char cc[l];
    for (int i = 0; i < l; ++i) {
        cc[i] = (char) (str[i] ^ 9);
    }


    printf("=================%s", cc);
    (*env)->ReleaseStringUTFChars(env, str_, str);

    return getUTF8JString(env, cc);
}

JNIEXPORT jstring JNICALL
Java_com_ndkdev_utils_NDKUtilsJ_decrypt(JNIEnv *env, jclass type, jstring str_) {
    const char *str = (*env)->GetStringUTFChars(env, str_, 0);

    int l = (int) strlen(str);
    char cc[l + 1];
    for (int i = 0; i < l; ++i) {
        cc[i] = (char) (str[i] ^ 9);
    }
    cc[l] = '\0';
    (*env)->ReleaseStringUTFChars(env, str_, str);

    return getUTF8JString(env, cc);
}

JNIEXPORT jbyteArray JNICALL
Java_com_ndkdev_utils_NDKUtilsJ_getByteArray(JNIEnv *env, jclass type) {

    char *s = "asdfasdf";

    jbyteArray array = (*env)->NewByteArray(env, (jsize) strlen(s));
    (*env)->SetByteArrayRegion(env, array, 0, (jsize) strlen(s), s);

    return array;
}

//c向java抛出异常
JNIEXPORT jstring JNICALL
Java_com_ndkdev_utils_NDKUtilsJ_testException(JNIEnv *env, jclass type) {


    jfieldID fid = (*env)->GetFieldID(env, type, "testStr", "Ljava/lang/String;");
    //检测是否发生Java异常
    jthrowable exception = (*env)->ExceptionOccurred(env);

    if (exception != NULL) {
        //发生了异常。让java代码可以继续运行，清空异常信息
        (*env)->ExceptionClear(env);

        jclass newEx=(*env)->FindClass(env,"java/lang/IllegalArgumentException");
        (*env)->ThrowNew(env,newEx,"向java抛出异常");
    } else {
//        jclass strJcla=(*env)->FindClass(env,"java/lang/String");
//        jstring s = (*env)->GetObjectField(env, strJcla, fid);
        jstring s = (*env)->GetObjectField(env, type, fid);
//        jstring s=(*env)->NewStringUTF(env,"get get get");
        return s;
    }

    return NULL;

}
