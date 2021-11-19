package com.example.todo.model.domain

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "TODO")
@Parcelize
data class Todo(
    @PrimaryKey(autoGenerate = true) var id : Long? = null,
    @ColumnInfo(name = "todoStr") var todoStr : String? = null,
    @ColumnInfo(name = "regDate") var regDate : String? = null,
    @ColumnInfo(name = "updateDate") var updateDate : String? = null,
    @ColumnInfo(name = "isCheck") var isCheck : Boolean = false
) : Parcelable


