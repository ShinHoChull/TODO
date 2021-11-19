package com.example.todo.model.domain

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "GPS")
@Parcelize
data class GPS(
    @PrimaryKey(autoGenerate = true) var id : Long? = null,
    @ColumnInfo(name = "todo_num") var todoNumber : Long? = null,
    @ColumnInfo(name = "lat") var latDataStr : Double? = null,
    @ColumnInfo(name = "lng") var lngDataStr : Double? = null,
    @ColumnInfo(name = "reg_date") var regDateStr : String? = null
) : Parcelable