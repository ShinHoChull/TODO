package com.example.todo.repository

import com.example.todo.model.domain.GPS
import com.example.todo.model.gps_db.GpsDAO

class GpsRepository(
    private val gpsDao : GpsDAO
) {

    fun getGpsData(id : Long) = gpsDao.getGpsList(id)

    fun insertGpsData(obj : GPS) = gpsDao.insertGps(obj)

    fun getAllGpsData() : List<GPS> = gpsDao.getAllGpsList()

    fun getGpsOne(id : Long) : GPS? = gpsDao.getGpsOne(id)

}