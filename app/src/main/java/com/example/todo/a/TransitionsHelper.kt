package com.example.todo.a

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.telecom.Call
import androidx.core.app.ActivityCompat
import com.example.todo.a.recevier.ActivityRecognitionReceiver
import com.example.todo.common.Defines
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity

class TransitionsHelper(
    private val mContext : Context
    ) {

    private val TRANSITIONS_RECEIVER_ACTION = "1"
    private val transitions = mutableListOf<ActivityTransition>()

    private var callBackListener: CallBackListener? = null

    public interface CallBackListener {
        fun successfulListener(msg : String)
        fun failListener(msg : String)
    }

    init {
        //자동차
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        //자전
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        //달리기
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        //달리기
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        //걷기
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        //휴식..
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        setUpTransition()
    }

    public fun setCallBackListener(listener: CallBackListener) {
        this.callBackListener = listener
    }


    private fun setUpTransition() {

        val request = ActivityTransitionRequest(transitions)
        val i2 = Intent(mContext, ActivityRecognitionReceiver::class.java)
        val pi2 = PendingIntent.getBroadcast(mContext, 0, i2, 0)

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ) {
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                val task = ActivityRecognition.getClient(mContext)
                    .requestActivityTransitionUpdates(request, pi2)

                task.addOnSuccessListener {
                    // Handle success
                    Defines.log("Transitions API was successfully registered")
                    callBackListener?.successfulListener("Transitions API was successfully registered")

                }

                task.addOnFailureListener { e: Exception ->
                    // Handle error
                    callBackListener?.successfulListener("Transitions API was Fail registered ${e.message.toString()}")
                }
            }
        }
    }

    public fun callReceiver() {
        val receiver = ActivityRecognitionReceiver()
        mContext.registerReceiver(receiver , IntentFilter(TRANSITIONS_RECEIVER_ACTION))
    }



}