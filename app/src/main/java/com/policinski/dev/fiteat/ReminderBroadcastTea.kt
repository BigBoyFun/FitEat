package com.policinski.dev.fiteat

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcastTea: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        // Create an explicit intent for an Activity in your app
        val intent2: Intent = Intent(context, MainActivity::class.java).apply {
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0)

        val builder = context?.let {
            NotificationCompat.Builder(it, "Tea")
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentTitle("Time for Tea!!!")
                .setContentText("Don't waste your opportunity for eat something great!!!")
                .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent)
                .setAutoCancel(true)
        }

        with(NotificationManagerCompat.from(context!!)) {
            // notificationId is a unique int for each notification that you must define
            notify(4, builder?.build()!!)
        }


    }
}