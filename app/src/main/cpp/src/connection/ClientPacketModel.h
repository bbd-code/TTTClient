//
// Created by ilya on 06.07.2026.
//

#ifndef TTTCLIENT_CLIENTPACKETMODEL_H
#define TTTCLIENT_CLIENTPACKETMODEL_H


#include "json.hpp"

namespace ttt_json_helpers {

    class ClientPacketModel : public nlohmann::json {

    public:
        ClientPacketModel() : nlohmann::json({
         {"info",            "response-from-client"},
         {"secret",          "ttt-client-android"},
         {"operation-state", nullptr},
         {"command",         nullptr},
         {"data",            nullptr}
                                     }) {}

    };
}

#endif //TTTCLIENT_CLIENTPACKETMODEL_H
