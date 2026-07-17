package com.bbd.tttclient.local.game.ai

import com.bbd.tttclient.local.game.logic.GameField
import com.bbd.tttclient.local.game.logic.Side
import kotlin.random.Random

class EasyBot : Bot {
    override fun processGameField(gameField: GameField) : Int {


        var index = generateRandomIndex()
        while (gameField.getCell(index).getSide() != Side.Unknow){

            index = generateRandomIndex()
        }

        return index
    }

    private fun generateRandomIndex() : Int{
        return Random.nextInt(0,9)
    }


}