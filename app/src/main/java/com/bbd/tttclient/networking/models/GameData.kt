package com.bbd.tttclient.networking.models

data class GameData(val cellIndex: Int, val cellSide: String ,val gameState: String, val winComb: Triple<Int, Int, Int>?)
