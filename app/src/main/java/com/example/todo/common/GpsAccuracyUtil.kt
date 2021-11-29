package com.example.todo.common

import com.example.todo.R
import com.example.todo.a.service.MyService3
import java.util.*
import kotlin.math.*

class GpsAccuracyUtil(
    private val startTime : Date //DB입력 시간
    , private val endTime : Date //현재 시간
    , private val intervalTime : Double
) {

    //DB 입력이 안되도 허용되는 수 ( intervalTime * lossCount )
    private val lossCount = 5

    //두 지점간의 거리 (Metre)
    private var distanceMetre : Int = 0

    //걸음 최소이동 (Metre)
    private var walkMinMovement : Int = 2
    //걸음 최대이동 (Metre)
    private var walkMaxMovement : Int = 200

    //이동수단 최소이동 (Metre)
    private var transMinMovement : Int = 5

    //이동수단 최대이동 (Metre)
    private var transMaxMovement : Int = 2000


    private val r = 6372.8 * 1000

    /**
     * 데이터가 입력이 안되도 허용되는 시간 계산 (분)
     */
    fun lossTime() : Double {
        return (intervalTime/1000/60) * lossCount
    }

    /**
     * DB에 입력된 시간과 입력이 안된 시간 차이 계산 (분)
     */
    fun differenceTime() : Double {
        return (endTime.time.minus(startTime.time)).toDouble()/1000/60
    }

    /**
     * 위경도 입력안되는 허용치 시간이 넘었는지?
     */
    fun isTimeDifference() : Boolean {
        if (lossTime() < differenceTime()) {
            return true
        }
        return false
    }


    /**
     * 두 좌표의 거리를 계산한다.
     *
     * @param lat1 위도1
     * @param lon1 경도1
     * @param lat2 위도2
     * @param lon2 경도2
     * @return 두 좌표의 거리(m)
     */
    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(lat1)) * cos(
            Math.toRadians(lat2)
        )
        val c = 2 * asin(sqrt(a))
        this.distanceMetre = (r * c).toInt()
        return this.distanceMetre
    }

    /**
     * 최소 최대로 이동한 경로 체크
     *  walkMinMovement 보다 이동 하지 않으면 제외.
     *  walkMaxMovement 보다 많이 이동하면 제외
     *
     */
    fun isWorkMinMaxMovement() : Boolean {
        Defines.log("distance-> $distanceMetre")
        if ( this.distanceMetre > walkMinMovement && this.distanceMetre < walkMaxMovement ) {
            return true
        }
        return false
    }

    /**
     * 최소 최대로 이동한 경로 체크
     *  transMinMovement 보다 이동 하지 않으면 제외.
     *  transMaxMovement 보다 많이 이동하면 제외
     *
     */
    fun isTransMinMaxMovement(): Boolean {
        Defines.log("distance-> $distanceMetre")
        if ( this.distanceMetre > transMinMovement && this.distanceMetre < transMaxMovement ) {
            return true
        }
        return false
    }


    fun isMinMovement() : Boolean {
        return (this.distanceMetre < walkMinMovement)
    }


    /**
     *
    1. 앱이 실행되면 현재 센서 수치를 저장한다. ( previousStep )
    2. 00시나 23시 59분에 센서 수치를 한번 더 측정한다. ( currentStep )
    3. 현재수치에서 저장한 수치를 뺀다 ( currentStep - previousStep ) 여기서 오늘 걸음 수가 나온다. ( todayStep )
    4. 현재 수치를 다시 저장한다. ( previousStep = currentStep )
    5. 2~4번을 반복한다.
     */
    fun saveWorkingCount(previousStep : Int , currentStep : Int , csp : Custom_SharedPreferences) : Int {

        if (csp.getValue("previousStep",-1) == -1) {
            //현재 걸음 수치.
            csp.put("previousStep",previousStep)
        }
        val todayWalk = currentStep - csp.getValue("previousStep",0)
        csp.put("todayStep",todayWalk)

        return todayWalk
    }


}