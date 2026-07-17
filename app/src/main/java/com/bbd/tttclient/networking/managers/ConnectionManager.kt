package com.bbd.tttclient.networking.managers

import com.bbd.tttclient.connectors.MultiplayerConnector
import com.bbd.tttclient.networking.tcp_connection.TCPConnection

object ConnectionManager {

    private val multiplayerConnector = MultiplayerConnector()

    fun connect(){
        multiplayerConnector.onStart()
    }

    fun getRooms(){
        multiplayerConnector.getRooms()
    }

    fun inRoom(roomId: Long){
        multiplayerConnector.inRoom(roomId)
    }

    fun outRoom(){
        multiplayerConnector.outRoom()
    }

    fun turn(index: Int){
        multiplayerConnector.turn(index)
    }

    fun disconnect(){
        multiplayerConnector.disconnect()
    }
}