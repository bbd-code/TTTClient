package com.bbd.tttclient.local.game.ai

import com.bbd.tttclient.local.game.logic.GameField

interface Bot {

    fun processGameField(gameField: GameField) : Int

}