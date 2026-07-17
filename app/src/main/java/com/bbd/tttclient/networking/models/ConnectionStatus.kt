package com.bbd.tttclient.networking.models

sealed class ConnectionStatus(val timeStamp: String){
    object Idle : ConnectionStatus("["+ java.time.Clock.systemUTC().instant() +"]")
    object ConnectedToServer : ConnectionStatus("["+ java.time.Clock.systemUTC().instant() +"]")
    data class GetRooms(val roomsData: List<RoomData>) : ConnectionStatus("["+ java.time.Clock.systemUTC().instant() +"]")
    data class ConnectedToRoom(val needWait: Boolean, val mySide: String) : ConnectionStatus("["+ java.time.Clock.systemUTC().instant() +"]")
    data class StartGame(val currentSide: String) : ConnectionStatus("["+ java.time.Clock.systemUTC().instant() +"]")
    data class TurnSuccess(val gameData: GameData) : ConnectionStatus("["+ java.time.Clock.systemUTC().instant() +"]")
    data class TurnError(val gameError: String) : ConnectionStatus("["+ java.time.Clock.systemUTC().instant() +"]")
    data class RoomChanged(val roomId: Long,val newConnections: Int) : ConnectionStatus("["+ java.time.Clock.systemUTC().instant() +"]")
    object Disconnected : ConnectionStatus("["+ java.time.Clock.systemUTC().instant() +"]")
    data class Error(val message: String) : ConnectionStatus("["+ java.time.Clock.systemUTC().instant() +"]")

}
