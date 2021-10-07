package com.example.todo.model.domain

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity
@Parcelize
data class Todo(
    @PrimaryKey(autoGenerate = true) val id : Long? = null,
    @ColumnInfo(name = "todoStr") val todoStr : String? = null,
    @ColumnInfo(name = "regDate") val regDate : String? = null,
    @ColumnInfo(name = "updateDate") val updateDate : String? = null,
    @ColumnInfo(name = "isCheck") val isCheck : Boolean = false
) : Parcelable
