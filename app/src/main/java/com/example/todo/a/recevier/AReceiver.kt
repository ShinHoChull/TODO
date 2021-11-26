package com.example.todo.a.recevier

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.common.Defines
import com.example.todo.common.getNowTimeToStr
import com.example.todo.repository.GpsRepository
import com.example.todo.repository.TodoRepository
import org.koin.android.ext.android.inject
import androidx.core.app.NotificationManagerCompat
import com.example.todo.a.service.MyService3


class AReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        Defines.log("hello broadcast~")

        //showNotification(context!!)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ain = Intent(context, MyService3::class.java)
            ain.putExtra("flag", "re")
            context?.startForegroundService(ain)
        } else {
            val ain = Intent(context, MyService3::class.java)
            ain.putExtra("flag", "re")
            context?.startService(ain)
        }

    }


    private fun showNotification(context : Context , str: String = "success") {
        // Create an explicit intent for an Activity in your app

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent
            .getActivity(
                context, 0, intent, 0
            )

        val channelId = "com.example.todo"
        val channelName = "My service3 channel"

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                channelId, channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }


        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("리시버 ~!${str}")
            .setContentText("regDate->${getNowTimeToStr()}")
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123, builder.build());

    }

}