package com.bbd.tttclient.connectors

import com.bbd.tttclient.networking.tcp_connection.TCPConnection

class MultiplayerConnector : GameConnector {


    private val tcpConnection = TCPConnection()


    override fun onStart() {
        //10.27.107.237
        // 10.198.68.237
        //10.198.68.237

        // 10.198.68.237
        // 192.168.0.5
        if(!tcpConnection.isConnected())
            tcpConnection.connect("192.168.0.3", 12345)

    }

    override fun turn(index: Int) {
        tcpConnection.turn(index)
    }


    fun getRooms(){
        tcpConnection.getRooms()
    }

    fun inRoom(roomId: Long){
        tcpConnection.inRoom(roomId)
    }

    fun outRoom(){
        tcpConnection.outRoom()
    }

    fun disconnect(){
        tcpConnection.disconnect()

    }

}