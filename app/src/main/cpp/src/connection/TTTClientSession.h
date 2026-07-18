//
// Created by ilya on 01.07.2026.

//



#ifndef TTTCLIENT_TTTCLIENTSESSION_H
#define TTTCLIENT_TTTCLIENTSESSION_H





#include <boost/asio/awaitable.hpp>
#include <boost/asio/use_awaitable.hpp>
#include <boost/asio/ip/tcp.hpp>
#include <boost/asio/io_context.hpp>
#include <boost/asio/redirect_error.hpp>
#include <boost/asio/read_until.hpp>
#include<boost/asio/write.hpp>
#include <boost/asio/streambuf.hpp>
#include <boost/asio/co_spawn.hpp>
#include <boost/asio/detached.hpp>
#include <android/log_macros.h>
#include <thread>
#include <iostream>
#include <string>
#include <src/JNICallback/JNICallback.h>
#include <memory>
#include "TTTClientSessionState.h"
#include <format>
#include "ClientPacketModel.h"
#include <boost/algorithm/string/trim.hpp>
#include"OperationState.h"
#include"Side.h"
#include "GameSate.h"


using boost::asio::awaitable;
using boost::asio::ip::tcp;
using boost::asio::io_context;
using boost::asio::redirect_error;
using boost::asio::use_awaitable;
using boost::asio::streambuf;
using boost::asio::detached;


#define LOGI(fmt, ...) \
__android_log_print(ANDROID_LOG_INFO, "TTTClientSession", "%s", \
std::format(fmt, ##__VA_ARGS__).c_str())

using nlohmann::json;


class TTTClientSession
{

public:



    TTTClientSession(std::unique_ptr<JNICallback> callback, const std::string& host, size_t port);


    void run();

    void request_get_rooms();
    void request_in_room(size_t room_id);
    void request_out_room();
    void request_turn(const int index);

    void stop();

private:


    awaitable<void> async_connect_to_server();
    awaitable<void> async_read_from_server();
    awaitable<void> async_disconnect();
    awaitable<void> async_write_to_server(const std::string& message);

private:
    //requests for commands
    awaitable<void> async_get_rooms();

    awaitable<void> async_in_room(size_t room_id);
    awaitable<void> async_out_room();
    awaitable<void> async_turn(int index);
    //handle for request from server
private:

    void handle_message(const std::string& json_message);
    void get_rooms(const json& json);
    void in_room(const json& json);
    void start_game(const json& json);
    void turn(const json& json);
    void room_changed(const json& json);
    void opponent_leave(const json& json);
    void must_leave_from_room(const json& json);

private:

    io_context io_context;
    std::unique_ptr<tcp::socket> socket;
    std::unique_ptr<JNICallback> callback;
    std::string host;
    size_t port{};
    streambuf read_buffer;
    TTTCSState session_state = TTTCSState::OFFLINE;

    std::map<std::string, std::function<void(const json& json)>> command_handlers;
    //это для отладки
    int tries_to_connect = 0;
    int session_id = 0;

};


#endif //TTTCLIENT_TTTCLIENTSESSION_H
