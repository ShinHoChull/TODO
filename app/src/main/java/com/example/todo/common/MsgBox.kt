package com.example.todo.common

import android.content.Context
import android.widget.Toast

class MsgBox (
    private val context : Context
        ) {

    fun baseShowToast(msg : String) {
        Toast.makeText(context , msg , Toast.LENGTH_SHORT).show()
    }


}