package com.example.todo.a.recevier

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.todo.a.service.MyService3
import com.example.todo.common.Defines
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

class ActivityRecognitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        Defines.log("hello recognitionReceiver~")

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent!!)!!
            for (event in result.transitionEvents) {

                var actionStr = ""
                if (event.activityType == DetectedActivity.STILL) {
                    actionStr = "정지상태"
                } else if (event.activityType == DetectedActivity.ON_FOOT) {
                    actionStr = "달리기"
                } else if (event.activityType == DetectedActivity.ON_BICYCLE) {
                    actionStr = "자전거"
                } else if (event.activityType == DetectedActivity.IN_VEHICLE) {
                    actionStr = "자동차"
                }  else if (event.activityType == DetectedActivity.WALKING) {
                    actionStr = "걷기"
                }

                // chronological sequence of events....
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val ain = Intent(context, MyService3::class.java)
                    ain.putExtra("flag", "recognition")
                    ain.putExtra("event",actionStr)
                    context?.startForegroundService(ain)
                } else {
                    val ain = Intent(context, MyService3::class.java)
                    ain.putExtra("flag", "recognition")
                    ain.putExtra("event",actionStr)
                    context?.startService(ain)
                }

                Defines.log("type->${event.activityType}")
            }
        }


    }

//    override fun onReceive(context: Context, intent: Intent) {
//
//        Defines.log("hello recognitionReceiver~")
//
//        if (ActivityTransitionResult.hasResult(intent)) {
//            val result = ActivityTransitionResult.extractResult(intent)!!
//            for (event in result.transitionEvents) {
//                // chronological sequence of events....
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    val ain = Intent(context, MyService3::class.java)
//                    ain.putExtra("flag", "recognition")
//                    context?.startForegroundService(ain)
//                } else {
//                    val ain = Intent(context, MyService3::class.java)
//                    ain.putExtra("flag", "recognition")
//                    context?.startService(ain)
//                }
//
//                Defines.log("type->${event.activityType}")
//            }
//        }
//    }
}