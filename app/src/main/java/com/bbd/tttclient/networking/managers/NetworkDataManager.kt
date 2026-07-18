package com.bbd.tttclient.networking.managers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bbd.tttclient.networking.models.ConnectionBridge


object NetworkDataManager {





    private val mutableConnectionBridge = MutableLiveData<ConnectionBridge>()
    val connectionBridge: LiveData<ConnectionBridge> = mutableConnectionBridge

    fun updateConnectionStatus(receivedConnectionBridge: ConnectionBridge){
        Log.w( "NetworkDataManager", "updateConnectionStatus called: $receivedConnectionBridge" )
        mutableConnectionBridge.postValue(receivedConnectionBridge)
    }

    fun setConnectionStatusValueToIdle(){
        mutableConnectionBridge.postValue(ConnectionBridge.Idle)
    }


}