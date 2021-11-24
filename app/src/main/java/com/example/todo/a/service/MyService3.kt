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
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.a.CurrentLocationComponent
import com.example.todo.common.*
import com.example.todo.model.domain.GPS
import com.example.todo.repository.GpsRepository
import com.example.todo.repository.TodoRepository
import com.example.todo.vm.AViewModel
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPoint
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.android.ext.android.inject
import java.util.*

class MyService3 : Service() {

    val mGpsRepository: GpsRepository by inject()
    val mTodoRepository: TodoRepository by inject()


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var currentLocationComponent: CurrentLocationComponent

    private val handler = Handler(Looper.getMainLooper())

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
            this.createLocationRequest()
        } else {
            Defines.log("error->70")
            stopSelf()
        }
        return START_STICKY
    }

    private fun setUpGPS() {


        currentLocationComponent = CurrentLocationComponent(applicationContext,
            {

                if (it != null) {

                    GlobalScope.launch(Dispatchers.IO) {
                        //JOB 사이즈 체크

                        val todoRows = mTodoRepository.getAllTodo()
                        if (todoRows.isNotEmpty()) {

                            for (i in todoRows.indices) {
                                val todoRow = todoRows[i]

                                val lastRow = mGpsRepository.getGpsOne(todoRow.id!!)
                                if (lastRow != null) {
                                    //마지막 저장된 위치
                                    val lastLat = lastRow.latDataStr!!.toDouble()
                                    val lastLng = lastRow.lngDataStr!!.toDouble()

                                    Defines.log("최근 데이터 입력 시간 ->${lastRow.regDateStr!!}")

                                    val start = getDateStrToDate(lastRow.regDateStr!!)?.time // 최근 입력된 날짜.
                                    val end = Date().time //현재 시간

                                    val current = end.minus(start!!)

                                    Defines.log("date -> " + "$start -" +
                                            "$end = ${current/1000/60}")

                                    //몇번까지 데이터 업데이트가 안되는것을 봐줄건가?
                                    val lossCount = 3

                                    //허용되는 losstime 가져오기.
                                    val lossTime = (INTERVAL_TIME/1000/60) * lossCount

                                    Defines.log("lossTime -> $lossTime")


                                    val currentMil = DistanceManager
                                        .getDistance(
                                            it.latitude
                                            , it.longitude
                                            , lastLat
                                            , lastLng
                                        )

                                    /*
                                        최근 입력된 데이터 시간과 현재 시간을 비교하여
                                        지정시간이 초과되면 입력을 한다?

                                        하지만 입력되는 현재 시간이 지정시간의 초과한 것에 비례하여
                                         과도하게 먼 거리면 입력하지 않는다.

                                         속도 계산은 어떻게?

                                         걷는건지 이동수단을 이용한지를 비교를??
                                     */




                                    //현재 위치랑 20m 차이가 안나면 데이터 저장 X (사람일 경우.)
                                    Defines.log("몇KM? ->${currentMil}")
                                    // || currentMil > 2000  <- 내일 시간 체크하고 잡기.
                                    if (currentMil < 5 || currentMil > 3000) {
                                        return@launch
                                    }
                                }

                                Defines.log("insert Data~")
                                mGpsRepository.insertGpsData(
                                    GPS(
                                        null,
                                        todoRow.id,
                                        it.latitude,
                                        it.longitude,
                                        getNowTimeToStr()
                                    )
                                )
                            }
                            showNotification()
                            Defines.log("size->" + mGpsRepository.getAllGpsData().size)
                        } else {
                            showNotification("miss")
                        }
                    }
                } else {
                    showNotification("miss-111")
                }
                //handler.postDelayed(runnable, INTERVAL_TIME)
                Defines.log("lat->${it.latitude} / lng -> ${it.longitude}")
            },
            {
               // handler.postDelayed(runnable, INTERVAL_TIME)
                showNotification("miss-115")
                Defines.log("${it}")
            }
        )
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
            handler.postDelayed(runnable, INTERVAL_TIME)
        }
    }

    private val runnable = Runnable {

        getCurrentLocation()

    }


    private fun getCurrentLocation() {
        Defines.log("getCurrentLocation!")
        currentLocationComponent.getCurrentLocation()
        handler.postDelayed(runnable, INTERVAL_TIME)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }

        IS_ACTIVITY_RUNNING = false
    }

    private fun showNotification(str: String = "success") {

        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent
            .getActivity(
                this, 0, intent, 0
            )

        val channelId = "com.example.todo"
        val channelName = "My service3 channel"

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
        var INTERVAL_TIME: Long = 30000
        var IS_ACTIVITY_RUNNING: Boolean = false
    }
}