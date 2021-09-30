package com.example.todo.b

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.todo.R
import com.example.todo.base.BaseFragment
import com.example.todo.common.Defines
import com.example.todo.databinding.FragmentBBinding
import com.example.todo.vm.AViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.todo.vm.BViewModel

//
class BFragment : BaseFragment<FragmentBBinding , AViewModel>(
    R.layout.fragment_b
) {

    override val viewModel: AViewModel by viewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Defines.log("getDataSize->${viewModel.todoList.value?.size}")

    }


}