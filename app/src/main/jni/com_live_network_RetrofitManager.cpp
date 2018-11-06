//
// Created by baoba on 2018/9/5.
//
#include<jni.h>
#include<stdio.h>
//导入我们创建的头文件
#include "com_live_network_RetrofitManager.h"

JNIEXPORT jstring JNICALL Java_com_live_network_RetrofitManager_getRemoteUrl(JNIEnv *env, jclass jclass){

    //返回一个字符串
    return env->NewStringUTF("http://35.201.172.238:8080/live-nyannyannyan/");
}
JNIEXPORT jstring JNICALL Java_com_live_network_RetrofitManager_getLocalDebugUrl(JNIEnv *env, jclass jclass){

    //返回一个字符串
    return env->NewStringUTF("http://192.168.191.1:8080/");
}
JNIEXPORT jstring JNICALL Java_com_live_network_RetrofitManager_getLocalUrl(JNIEnv *env, jclass jclass){

    //返回一个字符串
    return env->NewStringUTF("http://192.168.191.1:8080/live-nyannyannyan/");
}