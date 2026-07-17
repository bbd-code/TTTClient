//
// Created by ilya on 08.07.2026.
//

#include "JNIClassLoader.h"
#include "TTTClientSession.h"


jclass JNIClassLoader::get_class_from_java_classloader(JNIEnv *env, const char *class_name) {




    auto loaded_class = (jclass)(env->CallObjectMethod(java_class_loader, class_loader_load_method,env->NewStringUTF(class_name)));

    return loaded_class;

}

JNIClassLoader::JNIClassLoader(JNIEnv *env, jobject class_loader) {

    java_class_loader = env->NewGlobalRef(class_loader);

    class_loader_load_method = env->GetMethodID(env->GetObjectClass(java_class_loader), "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");

    if (class_loader_load_method){
        LOGI("classloader method find success");
    }

}

void JNIClassLoader::delete_java_class_loader(JNIEnv* env) {

    env->DeleteGlobalRef(java_class_loader);

}


