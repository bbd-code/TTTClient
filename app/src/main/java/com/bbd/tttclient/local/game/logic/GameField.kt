package com.bbd.tttclient.local.game.logic

import kotlin.random.Random
import kotlin.random.nextInt

class GameField {

    private var currentSide: Side = if(Random.nextInt(0,2) == 1) Side.Zero else Side.Cross

    private val cellsArray: Array<Cell> = Array(9) {index ->
        val row = index / 3
        val col = index % 3
        Cell(Side.Unknow, Pair(row, col), index)}

    fun getCells() : Array<Cell>{
        return cellsArray
    }

    fun getCell(rowCol: Pair<Int, Int>) : Cell? {
        return cellsArray.find{ cell ->
            (cell.getRowCol().first == rowCol.first && cell.getRowCol().second == rowCol.second)
        }
    }

    fun getCell(index: Int): Cell{
        return cellsArray[index]
    }

    fun getCurrentSide() : Side{
        return currentSide
    }

    fun setCurrentSide(side: Side){
        currentSide = side
    }
}