
#include <src/connection/TTTSessionManager.h>


extern "C"
JNIEXPORT void JNICALL
Java_com_bbd_tttclient_networking_tcp_1connection_TCPConnection_nativeConnect(JNIEnv *env,
                                                                 jobject thiz,
                                                                 jstring host,
                                                                 jint port,
                                                                 jobject class_loader,
                                                                 jobject callback) {

    JavaVM* jvm;


    env->GetJavaVM(&jvm);



    const char* host_str = env->GetStringUTFChars(host, nullptr);
    std::string host_cpp(host_str);
    env->ReleaseStringUTFChars(host, host_str);

    auto& manager = TTTSessionManager::instance();

    auto jni_callback_ptr = std::make_unique<JNICallback>(jvm, callback, class_loader);

    manager.start_session(std::move(jni_callback_ptr), host_cpp, port);


}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbd_tttclient_networking_tcp_1connection_TCPConnection_nativeGetRooms(JNIEnv *env, jobject thiz) {


    auto& manager = TTTSessionManager::instance();

    manager.request_get_rooms();

}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbd_tttclient_networking_tcp_1connection_TCPConnection_nativeInRoom(JNIEnv *env, jobject thiz, jlong room_id) {

    auto& manager = TTTSessionManager::instance();



    manager.request_in_room(static_cast<size_t>(room_id));

}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbd_tttclient_networking_tcp_1connection_TCPConnection_nativeTurn(JNIEnv *env, jobject thiz, jint index) {

    auto& manager = TTTSessionManager::instance();

    manager.request_turn(index);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bbd_tttclient_networking_tcp_1connection_TCPConnection_nativeOutRoom(JNIEnv *env, jobject thiz) {


    auto& manager = TTTSessionManager::instance();

    manager.request_out_room();


}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbd_tttclient_networking_tcp_1connection_TCPConnection_nativeDisconnect(JNIEnv *env, jobject thiz) {

    auto& manager = TTTSessionManager::instance();

    manager.stop();
}




