package com.bbd.tttclient.activities

import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.gridlayout.widget.GridLayout

import com.bbd.tttclient.R
import com.bbd.tttclient.connectors.BotGameConnector
import com.bbd.tttclient.connectors.GameConnector
import com.bbd.tttclient.connectors.LocalGameConnector
import com.bbd.tttclient.networking.managers.ConnectionManager
import com.bbd.tttclient.networking.managers.NetworkDataManager
import com.bbd.tttclient.networking.models.ConnectionBridge
import com.bbd.tttclient.networking.models.GameData

class GameActivity : AppCompatActivity() {



    private lateinit var uiCellsGridLayout : GridLayout

    private lateinit var stateTextView: TextView

    private lateinit var restartButton: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)
        init()

    }

    override fun onDestroy() {
        super.onDestroy()

        if(intent.getStringExtra("game-mode") == "multiplayer") {

            NetworkDataManager.setConnectionStatusValueToIdle()
            ConnectionManager.outRoom()
        }
    }


    private fun init(){

        lateinit var gameConnector: GameConnector

        restartButton = findViewById(R.id.restartGameButton)
        stateTextView = findViewById(R.id.gameStateTextView)
        uiCellsGridLayout =  findViewById(R.id.uiCellsLayout)


        when(intent.getStringExtra("game-mode")){
            "1vs1" -> { gameConnector = LocalGameConnector(this) }
            "PvsBot" -> { gameConnector = BotGameConnector(this) }
            "multiplayer" -> {

                restartButton.visibility = View.GONE
                if(intent.getBooleanExtra("need-wait", false)){
                    stateTextView.setText(R.string.needWait)
                }

                if(intent.getStringExtra("my-side") == "Cross")
                    Toast.makeText(this, R.string.yourSideCross, Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this, R.string.yourSideZero , Toast.LENGTH_LONG).show()


                NetworkDataManager.connectionBridge.observe(this) { connectionStatus ->
                    handleConnectionStatus(connectionStatus)
                }


                return

            }
        }


        restartButton.setOnClickListener {
            recreate()
        }



        gameConnector.onStart()

        for(i in 0 until uiCellsGridLayout.childCount){

            val button = uiCellsGridLayout.getChildAt(i) as ImageButton

            button.setOnClickListener {
                gameConnector.turn(i)

            }

        }


    }

    fun showCell(index: Int, imageId: Int){
        val button = uiCellsGridLayout.getChildAt(index) as ImageButton
        button.setImageResource(imageId)
        button.setOnClickListener(null)
    }

    fun showState(state: Int){
        stateTextView.setText(state)
    }


    fun showWin(winComb: Triple<Int, Int, Int>){

        for(index in winComb.toList()){
            (uiCellsGridLayout.getChildAt(index) as ImageButton).background.setTint(getColor(R.color.green))
        }

        allButtonsListenersToNull()

    }

    fun showDraw(state: Int){
        showState(state)
        allButtonsListenersToNull()
    }

    private fun allButtonsListenersToNull(){
        for (i in 0 until uiCellsGridLayout.size){
            (uiCellsGridLayout.getChildAt(i) as ImageButton).setOnClickListener(null)
        }
    }

    private fun handleConnectionStatus(connectionBridge: ConnectionBridge){
        Log.w("GameActivity", "hash-code: ${connectionBridge.hashCode()} with timestamp ${connectionBridge.timeStamp}🔄 handleConnectionStatus called with: $connectionBridge")
        when(connectionBridge){

            is ConnectionBridge.StartGame -> {

                stateTextView.setText(if(connectionBridge.currentSide == "Cross") R.string.crossTurn else R.string.zeroTurn)

                for(i in 0 until uiCellsGridLayout.childCount){
                    val button = uiCellsGridLayout.getChildAt(i) as ImageButton

                    button.setOnClickListener {

                        ConnectionManager.turn(i)

                    }
                }

            }
            is ConnectionBridge.TurnSuccess -> {
                handleTurnAndShow(connectionBridge.gameData)
            }
            is ConnectionBridge.TurnError -> {
                Toast.makeText(this, getString(R.string.notYourTurn),Toast.LENGTH_LONG).show()
            }

            is ConnectionBridge.Error -> {
                Log.e("GameActivity", "❌ Showing error toast: ${connectionBridge.message}")
                showError(connectionBridge.message)
            }
            is ConnectionBridge.Idle -> { return }
            else -> {}
        }
    }

    private fun showError(error: String){
        Toast.makeText(this, "Error : $error", Toast.LENGTH_LONG ).show()
    }

    fun handleTurnAndShow(gameData: GameData){

        if(gameData.winComb != null){

            showState(if(gameData.gameState == "cross-win") R.string.crossWin else R.string.zeroWin)
            showCell(gameData.cellIndex, if(gameData.cellSide == "Cross") R.drawable.cross else R.drawable.zero)
            showWin(gameData.winComb)
            return
        }

        if(gameData.gameState == "draw"){
            showCell(gameData.cellIndex, if(gameData.cellSide == "Cross") R.drawable.cross else R.drawable.zero)
            showState(R.string.draw);
            return
        }

        if(gameData.gameState == "game-continue"){
            showState(if(gameData.cellSide == "Cross") R.string.zeroTurn else R.string.crossTurn)

            showCell(gameData.cellIndex, if(gameData.cellSide == "Cross") R.drawable.cross else R.drawable.zero)
        }

    }

}