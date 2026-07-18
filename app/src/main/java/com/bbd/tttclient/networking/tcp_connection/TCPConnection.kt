package com.bbd.tttclient.networking.tcp_connection


import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.bbd.tttclient.networking.managers.NetworkDataManager
import com.bbd.tttclient.networking.models.ConnectionBridge
import com.bbd.tttclient.networking.models.GameData
import com.bbd.tttclient.networking.models.RoomData

class TCPConnection {

    private var connected: Boolean = false

    private val mainHandler = Handler(Looper.getMainLooper())


    companion object {
           init {
               System.loadLibrary("tttclient")
           }
       }

   private external fun nativeConnect(
       host: String, port: Int, classLoader: ClassLoader?,
       callback: Callback
   )

   private external fun nativeGetRooms()
   //TODO: исправить на интовый id

   private external fun nativeInRoom(roomId: Long)

   private external fun nativeOutRoom()

   private external fun nativeTurn(index: Int)

   private external fun nativeDisconnect()


   interface Callback {

       fun onConnectedToServer()

       fun onGetRooms(roomsList: List<RoomData>)


       fun onInRoom(needWait: Boolean, mySide: String)

       fun onOutRoom(operationState: Int)

       fun onTurn(index: Int, cellSide: String ,currentGameState: String, winComb: IntArray?)

       //TODO под вопросом
       fun onTurn(turnError: String)

//       fun onRoomChanged()

       fun onRoomChanged(roomId: Long, newConnections: Int)

       fun onStartGame(currentSide: String)

       fun onDisconnected()

       fun onError(error: String)


   }



   fun connect(host: String, port: Int){



       nativeConnect(host, port, javaClass.classLoader,object : Callback {

           @RequiresApi(Build.VERSION_CODES.S)
           override fun onConnectedToServer(){

               connected = true

               mainHandler.post {

                    NetworkDataManager.updateConnectionStatus(ConnectionBridge.ConnectedToServer)
               }

           }

           override fun onGetRooms(roomsList: List<RoomData>) {

               mainHandler.post {

                   NetworkDataManager.updateConnectionStatus(ConnectionBridge.GetRooms(roomsList))

               }

           }


           override fun onInRoom(needWait: Boolean, mySide: String) {
               mainHandler.post {
                   NetworkDataManager.updateConnectionStatus(
                       ConnectionBridge.ConnectedToRoom(needWait, mySide))
               }
           }


           override fun onOutRoom(operationState: Int) {
               TODO("Not yet implemented")
           }

           override fun onTurn(
               index: Int,
               cellSide: String,
               currentGameState: String,
               winComb: IntArray?
           ) {

               mainHandler.post {

                   NetworkDataManager.updateConnectionStatus(ConnectionBridge.TurnSuccess(GameData(index, cellSide, currentGameState,
                       if(winComb != null) Triple(winComb[0], winComb[1], winComb[2]) else null
                   )))

               }
           }

           override fun onTurn(turnError: String) {
               mainHandler.post {
                   NetworkDataManager.updateConnectionStatus(ConnectionBridge.TurnError(turnError))
               }
           }

           override fun onRoomChanged(roomId: Long, newConnections: Int) {
               mainHandler.post {
                   NetworkDataManager.updateConnectionStatus(ConnectionBridge.RoomChanged(roomId, newConnections))
               }
           }


           override fun onStartGame(currentSide: String) {
               mainHandler.post {
                   NetworkDataManager.updateConnectionStatus(
                       ConnectionBridge.StartGame(currentSide))
               }
           }


           override fun onDisconnected(){
               connected = false
           }

           @RequiresApi(Build.VERSION_CODES.S)
           override fun onError(error: String){

               connected = false

               mainHandler.post {
                    NetworkDataManager.updateConnectionStatus(ConnectionBridge.Error(error))
               }

           }
       } )
   }


    fun isConnected() : Boolean{
        return connected
    }

    fun getRooms(){

        nativeGetRooms()
    }

    fun inRoom(roomId: Long){
        nativeInRoom(roomId)
    }

    fun outRoom(){
        nativeOutRoom()
    }

    fun turn(index: Int){
        nativeTurn(index)
    }

   fun disconnect() {
       nativeDisconnect()
   }
}