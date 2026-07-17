//
// Created by ilya on 06.07.2026.
//

#ifndef TTTCLIENT_TTTSESSIONMANAGER_H
#define TTTCLIENT_TTTSESSIONMANAGER_H


#include "TTTClientSession.h"




class TTTSessionManager {

    std::mutex session_manager_mutex;

public:
    static TTTSessionManager& instance();

    void start_session(std::unique_ptr<JNICallback> callback, const std::string& host, size_t port);

    void request_get_rooms();
    void request_in_room(size_t room_id);
    void request_turn(int index);
    void request_out_room();
    void stop();

private:

    void start(std::unique_ptr<JNICallback>& callback,const std::string& host, size_t port);



private:

    TTTSessionManager() = default;
    std::shared_ptr<TTTClientSession> current_session;
    std::thread session_manager_tread;
};


#endif //TTTCLIENT_TTTSESSIONMANAGER_H
