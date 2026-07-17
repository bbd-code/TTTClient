package com.bbd.tttclient.networking.managers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import com.bbd.tttclient.networking.models.ConnectionStatus
import com.bbd.tttclient.networking.models.GameData
import com.bbd.tttclient.networking.models.RoomData
import kotlin.time.Clock


object NetworkDataManager {





    private val mutableConnectionStatus = MutableLiveData<ConnectionStatus>()
    val connectionStatus: LiveData<ConnectionStatus> = mutableConnectionStatus

    fun updateConnectionStatus(receivedConnectionStatus: ConnectionStatus){
        Log.w( "NetworkDataManager", "updateConnectionStatus called: $receivedConnectionStatus" )
        mutableConnectionStatus.postValue(receivedConnectionStatus)
    }

    fun setConnectionStatusValueToIdle(){
        mutableConnectionStatus.postValue(ConnectionStatus.Idle)
    }


}