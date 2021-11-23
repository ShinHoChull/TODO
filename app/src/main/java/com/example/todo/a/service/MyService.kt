package com.example.todo.a.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
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

class MyService : Service() {

    val mGpsRepository: GpsRepository by inject()
    val mTodoRepository: TodoRepository by inject()


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
            showNotification()
            this.setUpGPS()
        } else {
            stopSelf()
        }
        return START_STICKY
    }

    private fun setUpGPS() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    if (location != null) {
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
                                showNotification()
                                Defines.log("size->" + mGpsRepository.getAllGpsData().size)
                            } else {
                                stopSelf()
                                showNotification("miss")
                            }
                        }
                    } else {
                        showNotification("miss")
                    }


                    Defines.log("lat -> ${location.latitude} lng -> ${location.longitude} ${getNowTimeToStr()}")
                }
            }
        }

        this.createLocationRequest()
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
                interval = INTERVAL_TIME
                fastestInterval = INTERVAL_TIME
                priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val client: SettingsClient = LocationServices.getSettingsClient(applicationContext)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
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
    }


    companion object {
        const val TAG = "MyGpsService"
        var INTERVAL_TIME : Long = 60000
        var IS_ACTIVITY_RUNNING  : Boolean = false
    }
}