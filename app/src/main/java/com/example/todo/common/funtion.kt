package com.example.todo.common

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*
import kotlin.random.Random

fun getRandNum(from: Int = 0, to: Int = 0): Int {
    return Random.nextInt(to - from) + from
}

fun getNowTimeToStr(formatStr: String = "yyyy-MM-dd HH:mm:ss"): String {
    return SimpleDateFormat(formatStr)
        .format(System.currentTimeMillis())
}

fun getDateStrToDate(date: String) : Date? {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").run {
        parse(date)
    }
}


object DistanceManager {

    private const val R = 6372.8 * 1000

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
        return (R * c).toInt()
    }
}

private fun deg2rad(deg: Double): Double {
    return deg * Math.PI / 180.0
}

private fun rad2deg(rad: Double): Double {
    return rad * 180.0 / Math.PI
}