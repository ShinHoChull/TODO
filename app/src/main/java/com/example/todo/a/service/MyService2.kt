package com.example.todo.a.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.common.Defines
import com.example.todo.common.MsgBox
import com.example.todo.common.getNowTimeToStr
import com.example.todo.model.domain.GPS
import com.example.todo.repository.GpsRepository
import com.example.todo.repository.TodoRepository
import com.example.todo.vm.AViewModel
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.android.ext.android.inject

class MyService2 : Service() {

    val mGpsRepository: GpsRepository by inject()
    val mTodoRepository: TodoRepository by inject()

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onBind(intent: Intent): IBinder {
        TODO("입력을 해주세용 .")
    }

    override fun onCreate() {
        super.onCreate()
        IS_ACTIVITY_RUNNING = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val flag = intent?.getStringExtra("flag")

        if (flag.equals("start")) {
            Defines.log("GPS 실행합니다.")
            this.setUpGPS()
        } else {
            Defines.log("serviceDie->65")
            stopSelf()
        }
        return START_STICKY
    }

    private fun setUpGPS() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        this.createLocationRequest()
    }


    private val runnable = Runnable {

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Defines.log("serviceDie->84")
            stopSelf()
            Toast.makeText(
                applicationContext, "ACCESS_FINE_LOCATION not permission", Toast.LENGTH_SHORT
            ).show()
        } else if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                applicationContext, "ACCESS_COARSE_LOCATION not permission", Toast.LENGTH_SHORT
            ).show()
            Defines.log("serviceDie->95")
            stopSelf()
        } else {
            getLastLocation()
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            if (location != null) {
                Defines.log("lat -> ${location.latitude} lng -> ${location.longitude} ${getNowTimeToStr()}")
                GlobalScope.launch(Dispatchers.IO) {

                        //JOB 사이즈 체크
                        val todoRows = mTodoRepository.getAllTodo()
                        if (todoRows.isNotEmpty()) {
                            for ( i in todoRows.indices ) {
                                val todoRow = todoRows[i]

                                mGpsRepository.insertGpsData(
                                    GPS(null
                                    ,todoRow.id
                                    , location.latitude
                                    , location.longitude
                                    , getNowTimeToStr()
                                    )
                                )
                            }

                            Defines.log("size->" + mGpsRepository.getAllGpsData().size)
                        } else {
                            Defines.log("serviceDie->127")
                            stopSelf()
                        }
                    }
                showNotification()
            } else {
                showNotification("miss")
            }

            //handler.postDelayed(runnable,INTERVAL_TIME)
        }
    }

    private fun createLocationRequest() {

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Toast.makeText(
                applicationContext, "ACCESS_FINE_LOCATION not permission", Toast.LENGTH_SHORT
            ).show()

        } else if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Toast.makeText(
                applicationContext, "ACCESS_COARSE_LOCATION not permission", Toast.LENGTH_SHORT
            ).show()

        } else {

            val locationRequest = LocationRequest.create().apply {
                interval = 60000
                fastestInterval = 60000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val client: SettingsClient = LocationServices.getSettingsClient(applicationContext)
            //val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            handler.postDelayed(runnable,INTERVAL_TIME)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        IS_ACTIVITY_RUNNING = false
    }

    private fun showNotification(str : String = "success") {

        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent
            .getActivity(this
                , 0
                , intent
                , 0)

        val channelId = "com.codechacha.todoService"
        val channelName = "My service channel"

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                channelId, channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            var manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }


        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("위치 정보 저장중!${str}")
            .setContentText("regDate->${getNowTimeToStr()}")
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        //알림표시
        startForeground(1, builder.build())

        handler.postDelayed(runnable,INTERVAL_TIME)
    }


    companion object {
        const val TAG = "MyGpsService2"
        var INTERVAL_TIME : Long = 60000
        var IS_ACTIVITY_RUNNING  : Boolean = false
    }
}