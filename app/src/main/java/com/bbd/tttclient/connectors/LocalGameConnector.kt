package com.bbd.tttclient.connectors

import com.bbd.tttclient.R
import com.bbd.tttclient.activities.GameActivity
import com.bbd.tttclient.local.game.logic.Game
import com.bbd.tttclient.local.game.logic.GameState
import com.bbd.tttclient.local.game.logic.Side


class LocalGameConnector(private val activity: GameActivity) : GameConnector {



    private val game  = Game()

    override fun onStart() {
        activity.showState(if(
            game.getCurrentSide() == Side.Cross
        )
            R.string.crossTurn else R.string.zeroTurn)
    }


    override fun turn(index: Int)  {

        game.turn(index)

        activity.showCell(index, if(game.getCurrentSide() == Side.Cross) R.drawable.cross else R.drawable.zero)

        when(game.checkState()){

            GameState.CROSS_WIN -> {
                activity.showWin(game.getWinComb())
                activity.showState(R.string.crossWin)
            }

            GameState.ZERO_WIN -> {
                activity.showWin(game.getWinComb())
                activity.showState(R.string.zeroWin)
            }

            GameState.DRAW -> {

                activity.showDraw(R.string.draw)
            }

            GameState.GAME_CONTINUE -> {
                activity.showState(if(
                    game.getCurrentSide() == Side.Cross
                    )
                    R.string.crossTurn else R.string.zeroTurn)
            }

        }

    }
}