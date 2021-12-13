package com.example.todo.a.recevier

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.todo.a.service.MyService3
import com.example.todo.a.service.TransitionEnum
import com.example.todo.common.Defines
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

class ActivityRecognitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        Defines.log("hello recognitionReceiver~")

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent!!)!!
            for (event in result.transitionEvents) {

                // chronological sequence of events....
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val ain = Intent(context, MyService3::class.java)
                    ain.putExtra("flag", "recognition")
                    ain.putExtra("transitionType", event.activityType)
                    context?.startForegroundService(ain)
                } else {
                    val ain = Intent(context, MyService3::class.java)
                    ain.putExtra("flag", "recognition")
                    ain.putExtra("transitionType", event.activityType)
                    context?.startService(ain)
                }

            }
        }
    }
}