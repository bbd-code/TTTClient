//
// Created by ilya on 01.07.2026.
//

#ifndef TTTCLIENT_JNICALLBACK_H
#define TTTCLIENT_JNICALLBACK_H


#include <JNIClassLoader.h>
#include <string>



class JNICallback {

public:
    JNICallback(JavaVM* jvm, jobject callback, jobject jclass_loader);
    ~JNICallback();

    void on_connected_to_server();
    void on_get_rooms_response(std::vector<std::tuple<std::string, int, size_t>>& rooms_data);
    void on_in_room_response(bool need_wait, const std::string& my_side);
    void on_start_game(const std::string& current_side);
    void on_turn_response_success(int cell_index, const std::string& cell_side, const std::string& current_game_state, std::tuple<int, int, int>* win_comb);
    void on_turn_response_error(const std::string& game_error);
    void on_room_changed(size_t room_id, int new_connections);
    void on_disconnected();
    void on_error(const std::string& message);

private:
    JavaVM* jvm;
    JNIClassLoader class_loader;
    jobject callback_obj;
    jmethodID on_connected_to_server_method;
    jmethodID on_get_rooms_method;
    jmethodID on_in_room_method;
    jmethodID on_out_room_method;
    jmethodID on_start_game_method;
    jmethodID on_turn_method_success;
    jmethodID on_turn_method_error;
    jmethodID on_room_changed_connections;
    jmethodID on_disconnected_method;
    jmethodID on_error_method;

};


#endif //TTTCLIENT_JNICALLBACK_H
