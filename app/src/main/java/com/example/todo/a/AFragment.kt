package com.example.todo.a

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.Bundle

import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.todo.MainActivity

import com.example.todo.base.BaseFragment

import com.example.todo.databinding.FragmentABinding
import com.example.todo.vm.AViewModel
import com.example.todo.R
import com.example.todo.a.recevier.ActivityRecognitionReceiver
import com.example.todo.a.service.MyService
import com.example.todo.a.service.MyService2
import com.example.todo.a.service.MyService3

import com.example.todo.common.Defines
import com.example.todo.common.getNowTimeToStr
import com.example.todo.model.domain.Todo
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.fragment_a.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class AFragment : BaseFragment<FragmentABinding, AViewModel>(
    R.layout.fragment_a
) {

    override val viewModel: AViewModel by sharedViewModel()
    private lateinit var mAdapter: AdapterA

    val transitions = mutableListOf<ActivityTransition>()
    private val TRANSITIONS_RECEIVER_ACTION = "1"

    val t1 = ""
    val t2 = ""
    val t3 = ""



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        setUpVal()
        setUpObserver()
        setUpGPS()
       // requestGpsSettingChange()
        //setUpWorker()
        join_button.setOnClickListener {
            phoneCheckPermission()
        }
    }

    private fun phoneCheckPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            TedPermission.with(requireContext())
                .setPermissionListener(phoneListener)
                .setRationaleMessage("기기의 전화번호가 필요합니다.")
                .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다.\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                .setPermissions(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_PHONE_NUMBERS,

                    ).check()
        } else {
            TedPermission.with(requireContext())
                .setPermissionListener(phoneListener)
                .setRationaleMessage("기기의 전화번호가 필요합니다.")
                .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다.\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                .setPermissions(
                    Manifest.permission.READ_PHONE_STATE
                ).check()
        }
    }

    val phoneListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Defines.log("권한 허용함.")
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            Defines.log("권한 허용하지 않았음 전화번호 없이 회원가입 실행....")

        }
    }


    /*
    등록된 JOB 이 있는지 확인하고 있으면 서비스 실행.
     */
    private fun setUpGPS() {

        GlobalScope.launch(Dispatchers.IO) {
            if (viewModel.getAllData().isNotEmpty() && !MyService3.IS_ACTIVITY_RUNNING) {
                val intent = Intent(
                    requireContext(), MyService3::class.java
                ).apply {
                    putExtra("flag", "start")
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireActivity().startForegroundService(intent)
                } else {
                    requireActivity().startService(intent)
                }

            } else {
                Defines.log("not hello?")
            }
        }
    }

    private fun setUpVal() {

        AdapterA(ArrayList(), viewModel, requireActivity()).apply {
            list_view.adapter = this
            mAdapter = this
        }
    }

    override fun onResume() {
        super.onResume()



        GlobalScope.launch(Dispatchers.IO) {

            viewModel.getAllData().let {
                val arr = (it as ArrayList<Todo>).apply {
                    sortByDescending { it: Todo -> it.id }
                    viewModel.setTodoList(it)
                }
                mAdapter.setData(arr)
            }
        }
    }

    override fun onPause() {
        super.onPause()

    }

    private fun setUpObserver() {

        viewModel.removeList.observe(viewLifecycleOwner) {
            for (row in it) {
                mAdapter.delData(row)
            }
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
            .setContentTitle("fragment ~!${str}")
            .setContentText("regDate->${getNowTimeToStr()}")
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123, builder.build());

    }


}

