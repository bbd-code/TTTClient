package com.bbd.tttclient.networking.models

sealed class ConnectionBridge(val timeStamp: String){
    object Idle : ConnectionBridge("["+ java.time.Clock.systemUTC().instant() +"]")
    object ConnectedToServer : ConnectionBridge("["+ java.time.Clock.systemUTC().instant() +"]")
    data class GetRooms(val roomsData: List<RoomData>) : ConnectionBridge("["+ java.time.Clock.systemUTC().instant() +"]")
    data class ConnectedToRoom(val needWait: Boolean, val mySide: String) : ConnectionBridge("["+ java.time.Clock.systemUTC().instant() +"]")
    data class StartGame(val currentSide: String) : ConnectionBridge("["+ java.time.Clock.systemUTC().instant() +"]")
    data class TurnSuccess(val gameData: GameData) : ConnectionBridge("["+ java.time.Clock.systemUTC().instant() +"]")
    data class TurnError(val gameError: String) : ConnectionBridge("["+ java.time.Clock.systemUTC().instant() +"]")
    data class RoomChanged(val roomId: Long,val newConnections: Int) : ConnectionBridge("["+ java.time.Clock.systemUTC().instant() +"]")
    data class OpponentLeave(val needWait: Boolean) : ConnectionBridge("["+ java.time.Clock.systemUTC().instant() +"]")
    object MustLeaveFromRoom : ConnectionBridge("["+ java.time.Clock.systemUTC().instant() +"]")
    object Disconnected : ConnectionBridge("["+ java.time.Clock.systemUTC().instant() +"]")
    data class Error(val message: String) : ConnectionBridge("["+ java.time.Clock.systemUTC().instant() +"]")

}
