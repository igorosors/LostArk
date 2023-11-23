package com.example.lostark.data.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.lostark.R
import com.example.lostark.data.db.entity.EventEntity
import com.example.lostark.data.model.receiver.AlarmReceiver
import com.example.lostark.data.model.receiver.CloseReceiver
import com.example.lostark.data.model.receiver.SnoozeReceiver
import com.example.lostark.presentation.MainActivity

fun NotificationManager.sendNotification(message: String, id: Int, context: Context, eventEntity: EventEntity?) {

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        id,
        Intent(context, MainActivity::class.java),
        if (Build.VERSION.SDK_INT >= 23) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    )

    val snoozeIntent = Intent(context, SnoozeReceiver::class.java)
        .putExtra(AlarmReceiver.EXTRA_ID, id).putExtra(AlarmReceiver.EXTRA_MESSAGE, message)
        .putExtra(AlarmReceiver.EXTRA_EVENT, eventEntity)
    val closeIntent = Intent(context, CloseReceiver::class.java)
        .putExtra(AlarmReceiver.EXTRA_ID, id)

    val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        id,
        snoozeIntent,
        if (Build.VERSION.SDK_INT >= 23) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    )

    val closePendingIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        id,
        closeIntent,
        if (Build.VERSION.SDK_INT >= 23) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    )

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_timer)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(message)
        .setDefaults(Notification.DEFAULT_ALL)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_timer,
            context.getString(R.string.action_snooze),
            snoozePendingIntent
        )
        .addAction(
            R.drawable.ic_done,
            context.getString(R.string.action_close),
            closePendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(id, builder.build())
}


