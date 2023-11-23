package com.example.lostark.data.model.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.lostark.data.db.DatabaseClient
import com.example.lostark.data.util.sendNotification
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlarmReceiver: BroadcastReceiver() {
    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_EVENT = "extra_event"
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getIntExtra(EXTRA_ID, 0)
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            intent.getStringExtra(EXTRA_MESSAGE) ?: "null",
            id,
            context,
            intent.extras?.getParcelable(EXTRA_EVENT),
        )
        GlobalScope.launch(Dispatchers.IO) {
            DatabaseClient.getInstance(context).deleteEventById(id)
        }
    }

}