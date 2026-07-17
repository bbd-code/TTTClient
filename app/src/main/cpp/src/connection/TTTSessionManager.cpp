//
// Created by ilya on 06.07.2026.
//

#include "TTTSessionManager.h"

TTTSessionManager &TTTSessionManager::instance() {

    static TTTSessionManager instance;
    return instance;
}

void TTTSessionManager::start_session(std::unique_ptr<JNICallback> callback,const std::string& host, size_t port) {



    session_manager_tread = std::thread([this,  callback = std::move(callback), host, port]()mutable {

        start(callback, host, port);
    });

    session_manager_tread.detach();
}

void TTTSessionManager::start(std::unique_ptr<JNICallback>& callback,const std::string& host, size_t port) {

    std::lock_guard<std::mutex> lc(session_manager_mutex);

    current_session = std::make_shared<TTTClientSession>(std::move(callback), host, port);


    current_session->run();


}



void TTTSessionManager::stop() {

    current_session->stop();

}

void TTTSessionManager::request_get_rooms() {

    current_session->request_get_rooms();

}

void TTTSessionManager::request_in_room(size_t room_id) {

    current_session->request_in_room(room_id);

}

void TTTSessionManager::request_turn(int index) {
    current_session->request_turn(index);
}

void TTTSessionManager::request_out_room() {
    current_session->request_out_room();
}


