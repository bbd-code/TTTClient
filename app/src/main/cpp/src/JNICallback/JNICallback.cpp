//
// Created by ilya on 01.07.2026.
//

#include "JNICallback.h"
#include "src/connection/TTTClientSession.h"

JNICallback::JNICallback(JavaVM *jvm, jobject callback, jobject jclass_loader) : jvm(jvm){

    JNIEnv *env;



    this->jvm->GetEnv((void**)&env, JNI_VERSION_1_6);

    class_loader = JNIClassLoader(env, jclass_loader);

    callback_obj = env->NewGlobalRef(callback);

    jclass callback_class = env->GetObjectClass(callback);


    on_connected_to_server_method = env->GetMethodID(callback_class, "onConnectedToServer", "()V");
    on_get_rooms_method = env->GetMethodID(callback_class, "onGetRooms", "(Ljava/util/List;)V");
    on_in_room_method = env->GetMethodID(callback_class, "onInRoom", "(ZLjava/lang/String;)V");
    on_out_room_method = env->GetMethodID(callback_class, "onOutRoom", "(I)V");
    on_start_game_method = env->GetMethodID(callback_class, "onStartGame", "(Ljava/lang/String;)V");
    on_turn_method_success = env->GetMethodID(callback_class, "onTurn", "(ILjava/lang/String;Ljava/lang/String;[I)V");
    on_turn_method_error = env->GetMethodID(callback_class, "onTurn", "(Ljava/lang/String;)V");
    on_room_changed_connections = env->GetMethodID(callback_class,"onRoomChanged", "(JI)V");
    on_disconnected_method = env->GetMethodID(callback_class, "onDisconnected", "()V");
    on_error_method = env->GetMethodID(callback_class, "onError", "(Ljava/lang/String;)V");

    env->DeleteLocalRef(callback_class);

    if (on_connected_to_server_method &&
    on_get_rooms_method &&
    on_in_room_method &&
    on_out_room_method &&
    on_turn_method_success &&
    on_turn_method_error &&
    on_disconnected_method &&
    on_error_method) {
        LOGI("✅ All methods found successfully!");
    } else {
        LOGI("❌ Some methods are missing!");
    }

}

JNICallback::~JNICallback() {

    JNIEnv* env;

    jvm->AttachCurrentThread(&env, nullptr);

    if(env){

        env->DeleteGlobalRef(callback_obj);
        class_loader.delete_java_class_loader(env);
        LOGI("{}", "JNICallback and Classloader success delete");
    }



    jvm->DetachCurrentThread();


}



void JNICallback::on_connected_to_server(){

    JNIEnv* env;
    jvm->AttachCurrentThread(&env, nullptr);
    env->CallVoidMethod(callback_obj, on_connected_to_server_method);
    jvm->DetachCurrentThread();
}

[[maybe_unused]] void JNICallback::on_disconnected() {

    JNIEnv* env;
    jvm->AttachCurrentThread(&env, nullptr);
    env->CallVoidMethod(callback_obj, on_disconnected_method);
    jvm->DetachCurrentThread();
}

void JNICallback::on_error(const std::string &message){

    JNIEnv* env;

    jvm->AttachCurrentThread(&env, nullptr);
    jstring jmessage = env->NewStringUTF(message.c_str());
    env->CallVoidMethod(callback_obj, on_error_method, jmessage);
    env->DeleteLocalRef(jmessage);
    jvm->DetachCurrentThread();
}

void JNICallback::on_get_rooms_response(std::vector<std::tuple<std::string, int, size_t>> &rooms_data) {

    JNIEnv* env;

    jvm->AttachCurrentThread(&env, nullptr);

    jclass array_list_class_type = env->FindClass("java/util/ArrayList");
    jmethodID array_list_constructor = env->GetMethodID(array_list_class_type, "<init>", "()V");
    jmethodID array_list_add_method = env->GetMethodID(array_list_class_type, "add", "(Ljava/lang/Object;)Z");
    jobject array_list_rooms_data = env->NewObject(array_list_class_type, array_list_constructor);


    for(const auto& room_data_cpp : rooms_data)
    {
        jclass room_model_class_type = class_loader.get_class_from_java_classloader(env, "com/bbd/tttclient/networking/models/RoomData");


        jmethodID room_model_constructor = env->GetMethodID(room_model_class_type, "<init>",
                                                            "(Ljava/lang/String;IJ)V");

        env->ExceptionDescribe();

        jobject room_data = env->NewObject(room_model_class_type, room_model_constructor,
                                           env->NewStringUTF(std::get<0>(room_data_cpp).c_str()),
                                           static_cast<jint>(std::get<1>(room_data_cpp)),
                                           static_cast<jlong>(std::get<2>(room_data_cpp)));

        env->CallBooleanMethod(array_list_rooms_data, array_list_add_method, room_data);

        env->DeleteLocalRef(room_data);
        env->DeleteLocalRef(room_model_class_type);
    }

    env->DeleteLocalRef(array_list_class_type);
    env->CallVoidMethod(callback_obj, on_get_rooms_method, array_list_rooms_data);

    jvm->DetachCurrentThread();
}

void
JNICallback::on_in_room_response(bool need_wait, const std::string &my_side) {

    JNIEnv* env;
    jvm->AttachCurrentThread(&env, nullptr);
    jboolean jneed_wait = static_cast<jboolean>(need_wait);
    jstring jmyside = env->NewStringUTF(my_side.c_str());
    env->CallVoidMethod(callback_obj, on_in_room_method, jneed_wait, jmyside);


    env->DeleteLocalRef(jmyside);

    jvm->DetachCurrentThread();

}

void JNICallback::on_start_game(const std::string &current_side) {

    JNIEnv* env;
    jvm->AttachCurrentThread(&env, nullptr);


    env->CallVoidMethod(callback_obj, on_start_game_method, env->NewStringUTF(current_side.c_str()));


    jvm->DetachCurrentThread();
}

void JNICallback::on_turn_response_success(int cell_index, const std::string &cell_side,
                                   const std::string &current_game_state,
                                   std::tuple<int, int, int>* win_comb) {

    JNIEnv* env;
    jvm->AttachCurrentThread(&env, nullptr);

    jintArray jwinComb;

    if(win_comb)
    {
        jwinComb = env->NewIntArray(3);

        jint values[3]= {std::get<0>(*win_comb),std::get<1>(*win_comb), std::get<2>(*win_comb) };

        env->SetIntArrayRegion(jwinComb, 0, 3, values);


        env->CallVoidMethod(callback_obj, on_turn_method_success, static_cast<jint>(cell_index),
                            env->NewStringUTF(cell_side.c_str()), env->NewStringUTF(current_game_state.c_str()), jwinComb);
    }
    else
    {
        jwinComb = nullptr;
        env->CallVoidMethod(callback_obj, on_turn_method_success, static_cast<jint>(cell_index),
                            env->NewStringUTF(cell_side.c_str()), env->NewStringUTF(current_game_state.c_str()), jwinComb);
    }


    if(jwinComb)
        env->DeleteLocalRef(jwinComb);

    jvm->DetachCurrentThread();

}

void JNICallback::on_turn_response_error(const std::string &game_error) {

    JNIEnv* env;

    jvm->AttachCurrentThread(&env, nullptr);


    env->CallVoidMethod(callback_obj, on_turn_method_error, env->NewStringUTF(game_error.c_str()));


    jvm->DetachCurrentThread();

}

void JNICallback::on_room_changed(size_t room_id, int new_connections) {

    JNIEnv* env;

    jvm->AttachCurrentThread(&env, nullptr);


    env->CallVoidMethod(callback_obj, on_room_changed_connections, static_cast<jlong>(room_id), static_cast<jint>(new_connections));


    jvm->DetachCurrentThread();

}

