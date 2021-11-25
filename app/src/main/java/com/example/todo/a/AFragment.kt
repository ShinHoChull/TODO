package com.example.todo.a

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle

import android.view.*
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
) {

    override val viewModel: AViewModel by sharedViewModel()
    private lateinit var mAdapter: AdapterA

    private lateinit var workManager : WorkManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        setUpVal()
        setUpObserver()
        setUpGPS()
        //setUpWorker()
    }

    private fun setUpWorker() {
        this.workManager =  WorkManager.getInstance(requireContext())

        val data = workDataOf(Pair<String, Boolean>("isGoLocation", true))

        //한번 실행.
        var workRequest = OneTimeWorkRequestBuilder<LocationWorker>().setInputData(data).build()


        //최소설정 시간이 15분 임
        val locationWorkRequest = PeriodicWorkRequestBuilder<LocationWorker>(15
            , TimeUnit.MINUTES)
            .setInputData(data)
            .build()

        workManager.enqueue(workRequest)

        /*
           ExistingPeriodicWorkPolicy.KEEP     :  워크매니저가 실행중이 아니면 새로 실행하고, 실행중이면 아무작업도 하지 않는다.
           ExistingPeriodicWorkPolicy.REPLACE  :  워크매니저를 무조건 다시 실행한다.
        */
        //workManager.enqueue(OneTimeWorkRequest.from(BlurWorker::class.java))

//            .getInstance()
//            .enqueueUniquePeriodicWork(""
//            ,ExistingPeriodicWorkPolicy.KEEP
//            ,locationWorkRequest)

        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(viewLifecycleOwner, Observer { workInfo ->
                // Check if the current work's state is "successfully finished"
                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                    Defines.log(
                        "Success Worker->${
                            workInfo.outputData.getBoolean(
                                "success",
                                false
                            )
                        }"
                    )
                }
            })
    }

    /*
    등록된 JOB 이 있는지 확인하고 있으면 서비스 실행.
     */
    private fun setUpGPS() {
        GlobalScope.launch(Dispatchers.IO) {
            if (viewModel.getAllData().isNotEmpty() && !MyService3.IS_ACTIVITY_RUNNING) {
                Defines.log("hello?")
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

    private fun setUpObserver() {

        viewModel.removeList.observe(viewLifecycleOwner) {
            for (row in it) {
                mAdapter.delData(row)
            }
        }
    }



//    public boolean isServiceRunningCheck() {
//        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
//        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if ("ServiceName".equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }



}