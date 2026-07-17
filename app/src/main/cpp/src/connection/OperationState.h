//
// Created by ilya on 08.07.2026.
//

#ifndef TTTCLIENT_OPERATIONSTATE_H
#define TTTCLIENT_OPERATIONSTATE_H



enum OperationState
{
    SUCCESS,
    _ERROR_
};

enum GameError
{
    CANT_TURN_WAITING_FOR_OPPONENT,
    NOT_YOUR_TURN,
    CELL_OCCUPIED
};

#endif //TTTCLIENT_OPERATIONSTATE_H
