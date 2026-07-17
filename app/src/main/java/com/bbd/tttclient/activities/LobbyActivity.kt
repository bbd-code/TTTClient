package com.bbd.tttclient.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast

import com.bbd.tttclient.R
import com.bbd.tttclient.custom_views.RoomView
import com.bbd.tttclient.networking.managers.ConnectionManager
import com.bbd.tttclient.networking.managers.NetworkDataManager
import com.bbd.tttclient.networking.models.ConnectionStatus
import com.bbd.tttclient.networking.models.RoomData


class LobbyActivity : AppCompatActivity() {


    private lateinit var roomsLayout: LinearLayout;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_lobby)



        NetworkDataManager.connectionStatus.observe(this){  connectionStatus ->
            handleConnectionStatus(connectionStatus)

        }



        roomsLayout = findViewById(R.id.roomsLayout);

        ConnectionManager.connect()


    }

    override fun onResume() {
        super.onResume()



    }

    override fun onDestroy() {
        super.onDestroy()

        NetworkDataManager.setConnectionStatusValueToIdle()
        ConnectionManager.disconnect()
    }

    private fun handleConnectionStatus(connectionStatus: ConnectionStatus){
        Log.w("LobbyActivity", "hash-code: ${connectionStatus.hashCode()} with timestamp ${connectionStatus.timeStamp}🔄 handleConnectionStatus called with: $connectionStatus")
        when(connectionStatus){
            is ConnectionStatus.ConnectedToServer -> { showConnected() }
            is ConnectionStatus.GetRooms -> {
                showRooms(connectionStatus.roomsData)
            }
            is ConnectionStatus.ConnectedToRoom -> {
                showConnectedToRoom(connectionStatus.needWait, connectionStatus.mySide)
            }
            is ConnectionStatus.RoomChanged -> {
                showChangedRoom(connectionStatus.roomId, connectionStatus.newConnections)
            }

            is ConnectionStatus.Error -> {
                Log.e("LobbyActivity", "❌ Showing error toast: ${connectionStatus.message}")
                showError(connectionStatus.message)
            }
            else -> { return }
        }
    }




    private fun showConnectedToRoom(needWait: Boolean, mySide: String){

        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("need-wait",needWait)
        intent.putExtra("my-side", mySide);
        intent.putExtra("game-mode", "multiplayer")

        startActivity(intent)



    }

    private fun showRooms(roomsData: List<RoomData> ){

        for(roomData in roomsData){

            val roomView = RoomView(this)
            roomView.setRoomName(roomData.roomName)
            roomView.setConnections(roomData.connections)
            roomView.setRoomId(roomData.id);
            roomsLayout.addView(roomView)
            roomView.setOnClickListener {
                ConnectionManager.inRoom(roomData.id)
            }
        }

    }

    private fun showChangedRoom(roomId: Long, newConnections: Int){

        for(i in 0 until roomsLayout.childCount){
            val roomView = roomsLayout.getChildAt(i) as RoomView

            if(roomView.getRoomId() == roomId){

                roomView.setConnections(newConnections)
                return
            }

        }

    }


//добавить строку в "подключен" в файл со строками и другие
    private fun showConnected(){
        Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show()
    }

    private fun showDisconnected(){

    }

    private fun showError(error: String){
        Toast.makeText(this, "Error : $error", Toast.LENGTH_LONG ).show()
    }
}