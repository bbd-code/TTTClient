package com.bbd.tttclient.connectors

interface GameConnector {

    fun onStart()

    fun turn(index:Int)

}