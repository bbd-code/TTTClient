//
// Created by ilya on 01.07.2026.
//


#include "TTTClientSession.h"



TTTClientSession::TTTClientSession(std::unique_ptr<JNICallback> callback, const std::string& host,
                                   size_t port) :
                                   host(host),
                                   port(port)

{
    socket = std::make_unique<tcp::socket>(io_context);
    this->callback = std::move(callback);

    command_handlers["get-rooms"] = [this](const json& json) -> void
    {
        get_rooms(json);
    };
//    command_handlers["make-room"] = [this](const json& json) -> awaitable<void>
//    {
//        co_await self->make_room(json);
//    };
//    command_handlers["search-for-opponent"] = [this](const json& json) -> awaitable<void>
//    {
//        co_await self->search_for_opponent(json);
//    };
    command_handlers["start-game"] = [this](const json& json) -> void
    {
        start_game(json);
    };
    command_handlers["in-room"] = [this](const json& json) -> void
    {
        in_room(json);
    };
//    command_handlers["out-room"] = [this](const json& json) -> awaitable<void>
//    {
//        co_await self->out_room(json);
//    };
    command_handlers["turn"] = [this](const json& json) -> void
    {
        turn(json);
    };
    command_handlers["room-changed"] = [this](const json& json) -> void
    {
        room_changed(json);
    };

}



void TTTClientSession::run() {

    if(session_state == TTTCSState::OFFLINE) {

        ++session_id;

        if(session_state == TTTCSState::CONNECTING) {
            LOGI("Connecting wait...");
//            callback->on_message("Connecting...");
            return;
        }

        boost::asio::co_spawn(io_context, [this]() -> awaitable<void> {
            co_await async_connect_to_server();
        }, detached);


        LOGI("tries to connect : {}", ++tries_to_connect);
        io_context.run();
        LOGI("io_context stopped in io_thread, thread successfully stopped after detach");
        LOGI("tries to connect : {}", --tries_to_connect);


    }

}

awaitable<void> TTTClientSession::async_connect_to_server() {

    try {


        boost::system::error_code ec;

        session_state = TTTCSState::CONNECTING;

        //Подумать о реализации таймера


        co_await socket->async_connect(tcp::endpoint(boost::asio::ip::make_address(host), port),
                                       redirect_error(use_awaitable, ec));

        if (session_state == TTTCSState::CONNECTING && !ec)
        {
            std::string connect_log =
                    "Successfully connected to" + socket->remote_endpoint().address().to_string();
            LOGI("{}",connect_log);

            session_state = TTTCSState::ONLINE;
            callback->on_connected_to_server();

            co_await async_get_rooms();

//тут может быть сигфолт :), мб унаследовать std::enable_shared_from_this
            while (session_state == TTTCSState::ONLINE) {
                co_await async_read_from_server();
            }
            co_return;
        }

        std::ostringstream  oss;

        oss << "This thread id " << std::this_thread::get_id();

        std::string no_connected_log;
        LOGI("No connected : {}", oss.str());
        callback->on_error(ec.what() + " this session_id: " + std::to_string(session_id));
        co_await async_disconnect();

    }
    catch (const std::exception& ex)
    {
        callback->on_error(ex.what());

    }
}


awaitable<void> TTTClientSession::async_read_from_server() {

    boost::system::error_code ec;


    co_await boost::asio::async_read_until(*socket, read_buffer, '\n',redirect_error(use_awaitable, ec));




    if(!ec)
    {
        std::istream is(&read_buffer);
        std::string message;

        std::getline(is,message);

        message.erase(std::remove(message.begin(), message.end(), '\0'), message.end());

        boost::algorithm::trim_if(message, boost::is_any_of("\r\n\t "));

        handle_message(message);

    }
    else
    {
        callback->on_error(ec.what());
        co_await async_disconnect();
    }
}

awaitable<void> TTTClientSession::async_write_to_server(const std::string& message) {


    boost::system::error_code ec;
    co_await boost::asio::async_write(*socket, boost::asio::buffer(message + "\n") ,redirect_error(use_awaitable, ec));

    if(!ec)
    {

    }

}



awaitable<void> TTTClientSession::async_disconnect() {

    if (session_state == ONLINE) {

        ttt_json_helpers::ClientPacketModel exit_json; //клиент просит у сервера отключить его так правильно
        exit_json["command"] = "exit";
        co_await async_write_to_server(exit_json.dump());

    }

    if(socket && socket->is_open()) {
        boost::system::error_code ec;
        socket->cancel(ec);
        socket->close(ec);
    }


    session_state = TTTCSState::OFFLINE;
    io_context.stop();

    callback->on_disconnected();

    co_return;
}



//handle for message from server
void TTTClientSession::handle_message(const std::string &json_message) {

    try {
        json client_packet = json::parse(json_message);

        if (client_packet.contains("secret") &&
            client_packet.at("secret") == "ttt-online-server") {


            const std::string &command = client_packet.value("command", "");

            auto command_handle = command_handlers.find(command);

            if (command_handle != command_handlers.end()) {
                command_handle->second(client_packet);
            }

        }

    }
    catch (json::parse_error& parse_error_ex)
    {
        callback->on_error(parse_error_ex.what());

    }
}

/*get-rooms request*/

awaitable<void> TTTClientSession::async_get_rooms() {

    ttt_json_helpers::ClientPacketModel client_packet_get_rooms;

    client_packet_get_rooms["command"] = "get-rooms";

    co_await async_write_to_server(client_packet_get_rooms.dump());



}

void TTTClientSession::request_get_rooms() {
    boost::asio::post(io_context, [this]() {
        boost::asio::co_spawn(io_context, [this]() -> awaitable<void> {
            co_await async_get_rooms();}, detached);
    });
}

void TTTClientSession::get_rooms(const json& json){

    std::vector<std::tuple<std::string, int, size_t>> rooms_data;


    for(auto & room_data : json["data"]["rooms-data"]){


        rooms_data.emplace_back(room_data.value("name", ""), room_data.value("connections", 0), room_data.value("id", -1));

    }


    callback->on_get_rooms_response(rooms_data);


}



void TTTClientSession::stop() {
    if(session_state == TTTCSState::OFFLINE)
        return;
    boost::asio::post(io_context, [this]() {
        boost::asio::co_spawn(io_context, [this]() -> awaitable<void> {
            co_await async_disconnect();}, detached);
    });

}

awaitable<void> TTTClientSession::async_in_room(size_t room_id) {

    ttt_json_helpers::ClientPacketModel client_packet_in_room;

    client_packet_in_room["command"] = "in-room";
    client_packet_in_room["data"]["room-id"] = room_id;

    co_await async_write_to_server(client_packet_in_room.dump());

}

void TTTClientSession::request_in_room(size_t room_id) {

    boost::asio::post(io_context, [this, room_id](){
        boost::asio::co_spawn(io_context, [this, room_id]() -> awaitable<void>{

            co_await async_in_room(room_id);
        }, detached);
    });

}

void TTTClientSession::in_room(const json &json) {

    OperationState operation_state = static_cast<OperationState>(json.value("operation-state", 0));

    switch (operation_state)
    {
        case OperationState::_ERROR_:
            callback->on_error("room full");
            return;
        case OperationState::SUCCESS:
            bool need_wait = json["data"].value("need-wait", false);

            Side side = static_cast<Side>(json["data"].value("your-side", 0));
            need_wait ? callback->on_in_room_response(true, side == Side::Cross ? "Cross" : "Zero") :
                        callback->on_in_room_response(false, side == Side::Cross ? "Cross" : "Zero");

            return;
    }

}



void TTTClientSession::start_game(const json &json) {


    Side current_side = json["data"]["current-side"];

    callback->on_start_game(current_side == Side::Cross ? "Cross" : "Zero");

}

void TTTClientSession::request_turn(const int index) {

    boost::asio::post(io_context, [this, index](){
        boost::asio::co_spawn(io_context, [this, index]() -> awaitable<void>{

            co_await async_turn(index);
        }, detached);
    });
}

awaitable<void> TTTClientSession::async_turn(const int index) {

    ttt_json_helpers::ClientPacketModel client_packet_turn;

    client_packet_turn["command"] = "turn";
    client_packet_turn["data"]["cell-index"] = index;

    co_await async_write_to_server(client_packet_turn.dump());

}

void TTTClientSession::turn(const json &json) {

    if(static_cast<OperationState>(json.value("operation-state", 0)) == OperationState::SUCCESS)
    {
        GameState current_state = static_cast<GameState>(json["data"].value("game-state", 0));
        if(current_state != GameState::GAME_CONTINUE)
        {
            if(current_state == GameState::CROSS_WIN || current_state == GameState::ZERO_WIN){


                int index = json["data"].value("cell-index", 0);
                std::string cell_side = json["data"]["cell-side"]  == Side::Cross ? "Cross" : "Zero";
                std::string current_game_state = current_state == GameState::CROSS_WIN ? "cross-win" : "zero-win" ;

                auto winComb = json["data"]["win-comb"].get<std::tuple<int, int, int>>();

                callback->on_turn_response_success(index, cell_side, current_game_state, &winComb);

                return;
            }
            else{

                int index = json["data"].value("cell-index", 0);
                std::string cell_side = json["data"]["cell-side"]  == Side::Cross ? "Cross" : "Zero";
                std::string current_game_state = "draw";

                callback->on_turn_response_success(index, cell_side, current_game_state, nullptr);
                return;

            }

        }
        else
        {
            int index = json["data"].value("cell-index", 0);
            std::string cell_side = json["data"]["cell-side"]  == Side::Cross ? "Cross" : "Zero";
            std::string current_game_state = "game-continue";

            callback->on_turn_response_success(index, cell_side, current_game_state, nullptr);
            return;
        }


    }

    GameError game_error = static_cast<GameError>(json["data"].value("game-error", 0));

    switch (game_error) {

        case CANT_TURN_WAITING_FOR_OPPONENT:
            break;
        case NOT_YOUR_TURN:
            callback->on_turn_response_error("not-your-turn");
            break;
        case CELL_OCCUPIED:
            break;
    }


}

void TTTClientSession::request_out_room() {

    boost::asio::post(io_context, [this](){
        boost::asio::co_spawn(io_context, [this]() -> awaitable<void>{

            co_await async_out_room();
        }, detached);
    });

}

awaitable<void> TTTClientSession::async_out_room() {

    ttt_json_helpers::ClientPacketModel client_out_room_packet;

    client_out_room_packet["command"] = "out-room";

    co_await async_write_to_server(client_out_room_packet.dump());
}

void TTTClientSession::room_changed(const json &json) {

    if(json.contains("new-name")){

    }
    else{
        callback->on_room_changed(json["data"]["changed-room-data"].value("id", -1), json["data"]["changed-room-data"].value("new-connections", -1));
    }

}











