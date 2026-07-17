//
// Created by ilya on 08.07.2026.
//

#ifndef TTTCLIENT_JNICLASSLOADER_H
#define TTTCLIENT_JNICLASSLOADER_H

#include <jni.h>

class JNIClassLoader {

public:
    JNIClassLoader() = default;
    JNIClassLoader(JNIEnv* env, jobject class_loader);
    jclass get_class_from_java_classloader(JNIEnv* env, const char* class_name);
    void delete_java_class_loader(JNIEnv* env);

private:
    jobject java_class_loader;

    jmethodID class_loader_load_method;
};


#endif //TTTCLIENT_JNICLASSLOADER_H
