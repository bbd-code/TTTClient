package com.bbd.tttclient.local.game.logic

class Cell(private var side: Side, private var rowCol: Pair<Int, Int>, private var index: Int) {


    fun getIndex() : Int{
        return index
    }

    fun getRowCol() : Pair<Int, Int>{
        return rowCol
    }

    fun getSide(): Side{
        return side
    }

    fun setSide(side: Side){
        if(this.side == Side.Unknow)
            this.side = side
    }

}