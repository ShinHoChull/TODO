package com.example.todo.a

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.Observer
import androidx.work.*

import com.example.todo.base.BaseFragment

import com.example.todo.databinding.FragmentABinding
import com.example.todo.vm.AViewModel
import com.example.todo.R
import com.example.todo.a.service.MyService
import com.example.todo.a.service.MyService2
import com.example.todo.a.service.MyService3

import com.example.todo.common.Defines
import com.example.todo.model.domain.Todo
import kotlinx.android.synthetic.main.fragment_a.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class AFragment : BaseFragment<FragmentABinding, AViewModel>(
    R.layout.fragment_a
) , SensorEventListener {

    override val viewModel: AViewModel by sharedViewModel()
    private lateinit var mAdapter: AdapterA

    private lateinit var sensorManager : SensorManager
    private lateinit var stepCountSensor : Sensor



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        setUpVal()
        setUpObserver()
        setUpGPS()
        setUpSensor()
        //setUpWorker()
    }

    private fun setUpSensor() {

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager == null) {
            Toast.makeText(requireContext(), "noSensorManager Null", Toast.LENGTH_SHORT).show();
        }
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if(stepCountSensor == null) {
            Toast.makeText(requireContext(), "No Step Detect Sensor", Toast.LENGTH_SHORT).show();
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

        sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this);
    }

    private fun setUpObserver() {

        viewModel.removeList.observe(viewLifecycleOwner) {
            for (row in it) {
                mAdapter.delData(row)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if(event?.sensor?.getType() == Sensor.TYPE_STEP_COUNTER) {
           // Toast.makeText(requireContext(), "sensorChanger->${event.values[0]}", Toast.LENGTH_SHORT).show();
            Defines.log("sensorChange-> ${event.values[0]}")
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}