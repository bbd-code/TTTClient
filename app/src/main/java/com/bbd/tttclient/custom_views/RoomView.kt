package com.bbd.tttclient.custom_views

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.TextView
import com.bbd.tttclient.R


class RoomView(context: Context) : FrameLayout(context) {



    private val roomNameTextView: TextView
    private val changeableConnectionsTextView: TextView

    private var roomId: Long;

    init {

        LayoutInflater.from(getContext()).inflate(R.layout.room_layout, this, true);

        roomNameTextView = findViewById(R.id.roomName)
        changeableConnectionsTextView = findViewById(R.id.roomConnectionsQuantityConnetedNow)
        roomId = 0
        isClickable = true
        isContextClickable = true
    }

    fun setRoomName(roomName: String) {
        roomNameTextView.text = roomName
    }

    fun setConnections(connections: Int) {
        changeableConnectionsTextView.text = connections.toString()
    }

    fun setRoomId(roomId: Long){
        this.roomId = roomId
    }

    fun getRoomId() : Long{
        return roomId
    }

}