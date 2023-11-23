package com.example.lostark.data.model.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.text.format.DateUtils
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import com.example.lostark.data.db.DatabaseClient
import com.example.lostark.data.db.entity.EventEntity
import com.example.lostark.presentation.StartActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SnoozeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(AlarmReceiver.EXTRA_ID, 0)

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancel(id)

        val message = intent.getStringExtra(AlarmReceiver.EXTRA_MESSAGE)
        val eventEntity: EventEntity = intent.extras?.getParcelable(AlarmReceiver.EXTRA_EVENT) ?:
        EventEntity(
            id = id,
            name = message,
            time = "",
            gearScore = 0,
            day = 0,
            month = 0,
        )

        val triggerTime = System.currentTimeMillis() + DateUtils.MINUTE_IN_MILLIS
        val notifyIntent = Intent(context, AlarmReceiver::class.java)
            .putExtra(AlarmReceiver.EXTRA_ID, id)
            .putExtra(AlarmReceiver.EXTRA_MESSAGE, message)
            .putExtra(AlarmReceiver.EXTRA_EVENT, eventEntity)
        val notifyPendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            notifyIntent,
            if (Build.VERSION.SDK_INT >= 23) { PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT } else { PendingIntent.FLAG_UPDATE_CURRENT }
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val showIntent = Intent(context, StartActivity::class.java)
        val showOperation = PendingIntent.getActivity(
            context,
            0,
            showIntent,
            if (Build.VERSION.SDK_INT >= 23) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, showOperation)
        alarmManager.setAlarmClock(alarmClockInfo, notifyPendingIntent)

        GlobalScope.launch(Dispatchers.IO) {
            DatabaseClient.getInstance(context).saveEvent(listOf(eventEntity))
        }

    }

}