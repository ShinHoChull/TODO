package com.example.todo.a

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.todo.common.Defines

class LocationWorker(
    appContext : Context
    , private val workerParams : WorkerParameters
) : Worker(appContext , workerParams){

    override fun doWork(): Result {

        return try {
            // Do the work
            val response = lastLocation()

            val outputData = workDataOf(Pair<String,Boolean>("success",response))
            Defines.log("success!!")

            Result.success(outputData)
        } catch (e: Exception) {
            Defines.log("fail!!")
            Result.failure()
        }

    }

    private fun lastLocation() : Boolean {
        Defines.log("workerManager Get!!!!! ${workerParams.inputData.getBoolean("isGoLocation",false)}")


        return false
    }
}