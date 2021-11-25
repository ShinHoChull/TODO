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

    //최소이동 (Metre)
    private var minMovement : Int = 2

    //최대이동 (Metre)
    private var maxMovement : Int = 2000

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
     *  minMovement 보다 이동 하지 않으면 제외.
     *  maxMovement 보다 많이 이동하면 제외
     *
     */
    fun isMinMaxMovement() : Boolean {
        Defines.log("distance-> $distanceMetre")
        if ( this.distanceMetre > minMovement && this.distanceMetre < maxMovement ) {
            return true
        }
        return false
    }

    fun isMinMovement() : Boolean {
        return (this.distanceMetre < minMovement)
    }




}