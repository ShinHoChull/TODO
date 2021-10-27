package com.example.todo.a

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todo.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ABottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_a_fragment, container, false)

    companion object {
        const val TAG = "ModalBottomSheet"
    }

}