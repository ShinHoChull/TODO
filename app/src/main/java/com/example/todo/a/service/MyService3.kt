package com.example.todo.a.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.example.todo.a.recevier.AReceiver
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
import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo

import androidx.core.app.AlarmManagerCompat
import android.content.Intent.getIntent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.util.Log
import com.example.todo.a.TransitionsHelper
import com.example.todo.a.recevier.ActivityRecognitionReceiver


class MyService3 : Service() {

    val mGpsRepository: GpsRepository by inject()
    val mTodoRepository: TodoRepository by inject()

    val originTesting : String = ""

    val t1 : String = ""

    private lateinit var currentLocationComponent: CurrentLocationComponent
    private lateinit var locationCallback: LocationCallback
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var mCsp: Custom_SharedPreferences

    private val TRANSITIONS_RECEIVER_ACTION = "1"

    var lastLocation : Location? = null
    var eventStr : String = ""

    lateinit var mTransitionsHelper: TransitionsHelper

    override fun onBind(intent: Intent): IBinder {
        TODO("입력을 해주세용 .")
    }

    override fun onCreate() {
        super.onCreate()

        mCsp = Custom_SharedPreferences(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mTransitionsHelper = TransitionsHelper(applicationContext)

        //백그라운드에서 단독으로 수행할 GPS
        this.setUpLocationCallBack()
        this.startLocationUpdates()

        setUpTransitionHelper()

        IS_ACTIVITY_RUNNING = true
    }

    private fun setUpTransitionHelper() {
        mTransitionsHelper.callReceiver()

        mTransitionsHelper.setCallBackListener(object : TransitionsHelper.CallBackListener {
            override fun successfulListener(msg: String) {
                showNotification(msg)
            }

            override fun failListener(msg: String) {
                showNotification(msg)
            }
        })
    }

    private fun setUpLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    Defines.log("단독으로 수행하는 GPS->${location.latitude} / ${location.longitude} ")
                }
            }
        }
    }

    private fun createLocationRequestSetup() : LocationRequest {
        return LocationRequest.create().apply {
            interval = 1000 * 20
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.requestLocationUpdates(
            createLocationRequestSetup(),
            locationCallback,
            Looper.getMainLooper())
    }


    private fun setUpCspReset() {
        mCsp.put("oldStep", -1)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val flag = intent?.getStringExtra("flag")


        if (flag.equals("start")) {

            Defines.log("알림을 실행 ..")

            //setUpCspReset()
            this.showNotification()
            this.setUpGPS()
            this.createLocationRequest()
            this.getCurrentLocation()
            //scheduleAlarms(applicationContext)

        } else if (flag.equals("re")) {
            Defines.log("스케줄을 재실행 ..")

            //showNotification("현재 걸음 -> ${mCsp.getValue("todayStep",0)}")
            getCurrentLocation()
            scheduleAlarms(applicationContext)

        } else if (flag.equals("recognition")) {
            eventStr = if (intent?.getStringExtra("event") == null) {
                ""
            } else {
                intent.getStringExtra("event")!!
            }
        }else {
            Defines.log("error->70")
            stopSelf()
        }

        return START_STICKY
    }

    private fun scheduleAlarms(ctxt: Context) {
        val mgr = ctxt.getSystemService(ALARM_SERVICE) as AlarmManager

        val i = Intent(ctxt, AReceiver::class.java)
        val pi = PendingIntent.getBroadcast(ctxt, 0, i, 0)

        val i2 = Intent(ctxt, AReceiver::class.java)
        val pi2 = PendingIntent.getActivity(ctxt, 0, i2, 0)
        val ac = AlarmClockInfo(
            System.currentTimeMillis() + INTERVAL_TIME,
            pi2
        )
        mgr.setAlarmClock(ac, pi)
    }

    private fun setUpGPS() {

        currentLocationComponent = CurrentLocationComponent(applicationContext,
            {

//                if (lastLocation != null) {
//
//
//                    //시간간격
//                    val deltaTime = (it.time - lastLocation!!.time) / 1000.0;
//
//                    //속도 계산
//                    val speed = lastLocation!!.distanceTo(it) / deltaTime;
//
//                    //거리간격
//                    val geri = lastLocation!!.distanceTo(it)
//
//                    Defines.log("${String.format("%.1f",geri)} m /" +
//                            " ${String.format("%.1f", speed)} km / ${String.format("%.1f",deltaTime)} ")
//
//                    showNotification("${String.format("%.1f",geri)} m /" +
//                            " ${String.format("%.1f", speed)} km / ${String.format("%.1f",deltaTime)} ")
//
//
//                }
//
//                lastLocation = it

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

                                    val gpsA = GpsAccuracyUtil(
                                        getDateStrToDate(lastRow.regDateStr!!)!!,
                                        Date(),
                                        INTERVAL_TIME.toDouble()
                                    )

                                    val currentMil = gpsA.getDistance(
                                        it.latitude, it.longitude, lastLat, lastLng
                                    )

                                    val oldStep = mCsp.getValue("oldStep", -1)
                                    val todayStep = mCsp.getValue("todayStep", -1)
                                    if (oldStep == -1) {
                                        mCsp.put("oldStep", todayStep)
                                    }

                                    //걸어가고있나?
                                    if (todayStep > oldStep) {
                                        //걸음 동기화
                                        mCsp.put("oldStep",todayStep)

                                        if (gpsA.isWorkMinMaxMovement()) {
                                            Defines.log("insert Data~")
                                            showNotification("등록-walk $currentMil m / $todayStep")
                                            mGpsRepository.insertGpsData(
                                                GPS(
                                                    null,
                                                    todoRow.id,
                                                    it.latitude,
                                                    it.longitude,
                                                    getNowTimeToStr()
                                                )
                                            )
                                        } else {

                                            //허용 시간이 지나면 등록
                                            if (gpsA.isTimeDifference()) {
                                                showNotification("등록-walk $currentMil m timeOver")
                                                mGpsRepository.insertGpsData(
                                                    GPS(
                                                        null,
                                                        todoRow.id,
                                                        it.latitude,
                                                        it.longitude,
                                                        getNowTimeToStr()
                                                    )
                                                )
                                            } else {
                                                showNotification("미등록-walk $currentMil m / 걸음$todayStep 보")
                                           }
                                        }

                                    } else { // 이동수단을 이용하나?
                                        if (gpsA.isTransMinMaxMovement()) {
                                            Defines.log("insert Data~")
                                            showNotification("등록-trans $currentMil m / 걸음$todayStep 보 ")
                                            mGpsRepository.insertGpsData(
                                                GPS(
                                                    null,
                                                    todoRow.id,
                                                    it.latitude,
                                                    it.longitude,
                                                    getNowTimeToStr()
                                                )
                                            )
                                        } else {

                                            //허용 시간이 지나면 등록
                                            if (gpsA.isTimeDifference()) {
                                                showNotification("등록-trans $currentMil m timeOver")
                                                mGpsRepository.insertGpsData(
                                                    GPS(
                                                        null,
                                                        todoRow.id,
                                                        it.latitude,
                                                        it.longitude,
                                                        getNowTimeToStr()
                                                    )
                                                )
                                            } else {
                                                showNotification("미등록-trans $currentMil m")
                                            }
                                        }
                                    }

                                } else {
                                    showNotification()
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
                            }

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
                //handler.postDelayed(runnable, INTERVAL_TIME)
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
            scheduleAlarms(applicationContext)
        }
    }

    private fun getCurrentLocation() {
        Defines.log("getCurrentLocation!" + getNowTimeToStr())
        currentLocationComponent.getCurrentLocation()

        //showNotification()
        //handler.postDelayed(runnable, INTERVAL_TIME)
    }

    override fun onDestroy() {
        super.onDestroy()
//        if (fusedLocationClient != null) {
//            fusedLocationClient.removeLocationUpdates(locationCallback)
//        }

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
            .setContentTitle(str)
            .setContentText("${getNowTimeToStr()} / event $eventStr")
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        //알림표시
        startForeground(1, builder.build())
    }


    companion object {
        const val TAG = "MyGpsService"
        var INTERVAL_TIME: Long = 1000 * 60
        var IS_ACTIVITY_RUNNING: Boolean = false
    }


    /**
     *
    1. 앱이 실행되면 현재 센서 수치를 저장한다. ( previousStep )
    2. 00시나 23시 59분에 센서 수치를 한번 더 측정한다. ( currentStep )
    3. 현재수치에서 저장한 수치를 뺀다 ( currentStep - previousStep ) 여기서 오늘 걸음 수가 나온다. ( todayStep )
    4. 현재 수치를 다시 저장한다. ( previousStep = currentStep )
     */
    private fun saveWorkingCount(previousStep: Int, currentStep: Int): Int {

        if (mCsp.getValue("previousStep", -1) == -1) {
            //현재 걸음 수치.
            mCsp.put("previousStep", previousStep)
        }
        val todayWalk = currentStep - mCsp.getValue("previousStep", 0)
        mCsp.put("todayStep", todayWalk)

        return todayWalk
    }

}