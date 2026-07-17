package com.bbd.tttclient.connectors

import com.bbd.tttclient.R
import com.bbd.tttclient.activities.GameActivity
import com.bbd.tttclient.local.game.ai.Bot
import com.bbd.tttclient.local.game.ai.EasyBot
import com.bbd.tttclient.local.game.logic.Game
import com.bbd.tttclient.local.game.logic.GameState
import com.bbd.tttclient.local.game.logic.Side

class BotGameConnector(private val activity: GameActivity) : GameConnector{

    private lateinit var bot : Bot

    private val game  = Game()

    override fun onStart() {
        when(activity.intent.getStringExtra("bot-mode")){
            "easy-bot" -> {
                bot = EasyBot()
            }
            "medium-bot" -> {}
            "hard-bot" -> {}
        }

        activity.showState(if(
            game.getCurrentSide() == Side.Cross
        )
            R.string.crossTurn else R.string.zeroTurn)
    }


    override fun turn(index: Int)  {

        game.turn(index)
        activity.showCell(index, if(game.getCurrentSide() == Side.Cross) R.drawable.cross else R.drawable.zero)
        checkState()


        if(game.getCurrentGameState() == GameState.GAME_CONTINUE) {

            val indexAfterProcess = bot.processGameField(game.getGameField())
            game.turn(indexAfterProcess)
            activity.showCell(
                indexAfterProcess,
                if (game.getCurrentSide() == Side.Cross) R.drawable.cross else R.drawable.zero
            )
            checkState()
        }


    }


    private fun checkState(){
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