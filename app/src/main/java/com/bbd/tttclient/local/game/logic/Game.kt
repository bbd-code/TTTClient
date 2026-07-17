package com.bbd.tttclient.local.game.logic



class Game {

    private lateinit var winComb : Triple<Int, Int, Int>

    private lateinit var gameState: GameState

    private val gameField = GameField()

    private val arrayWinCombs = arrayOf(
        arrayOf(0, 1, 2),
        arrayOf(3, 4, 5),
        arrayOf(6 ,7, 8),
        arrayOf(0, 3, 6), // первый столбец
        arrayOf(1, 4, 7), // второй столбец
        arrayOf(2, 5, 8), // третий столбец
        arrayOf(0, 4, 8), // диагональ 1
        arrayOf(2, 4, 6)  // диагональ 2
    )

    private fun changeSide() {
        gameField.setCurrentSide(if(gameField.getCurrentSide() == Side.Cross) Side.Zero else Side.Cross)
    }

    fun turn(index: Int){

        gameField.getCell(index).setSide(gameField.getCurrentSide())

    }


    fun checkState(): GameState{

        for(winComb in arrayWinCombs){
            var allSame = true

            for(pos in winComb){
                val cell = gameField.getCell(pos)
                if(cell.getSide() != gameField.getCurrentSide()){
                    allSame = false
                    break
                }
            }
            if(allSame) {

                this.winComb = Triple(winComb[0], winComb[1], winComb[2])

                if (gameField.getCurrentSide() == Side.Cross) gameState = GameState.CROSS_WIN else gameState = GameState.ZERO_WIN

                return gameState

            }
        }

        var isFull = true

        for(i in 0 until 3){

            for(j in 0 until 3)
            {
                if(gameField.getCell(Pair(i, j))?.getSide() == Side.Unknow) {
                    isFull = false
                    break
                }
            }

            if(!isFull)
                break

        }

        if(isFull) {
            gameState = GameState.DRAW
            return gameState
        }
        changeSide()
        gameState = GameState.GAME_CONTINUE
        return gameState

    }


    fun getWinComb(): Triple<Int, Int, Int>{
        return winComb
    }


    fun getCurrentSide() : Side{
        return gameField.getCurrentSide()
    }

    fun getGameField() : GameField{
        return gameField
    }

    fun getCurrentGameState() : GameState{
        return gameState
    }
}