package com.example.todo.a

import android.app.Application
import android.os.Bundle

import android.view.*

import com.example.todo.base.BaseFragment

import com.example.todo.databinding.FragmentABinding
import com.example.todo.vm.AViewModel
import com.example.todo.R
import com.example.todo.common.Defines
import com.example.todo.common.MsgBox
import kotlinx.android.synthetic.main.fragment_a.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AFragment : BaseFragment<FragmentABinding, AViewModel>(
    R.layout.fragment_a
) {

    override val viewModel: AViewModel by viewModel()
    private val msgBox : MsgBox by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        setUpListener()
    }

    private fun setUpListener() {

        viewModel.todoList.observe(viewLifecycleOwner) {
            Defines.log("change list size - > ${viewModel.todoList.value?.size}")
            list_view.adapter?.notifyDataSetChanged()
            list_view.adapter = AdapterA(it)
        }

        viewModel.onClickButton.observe(viewLifecycleOwner) {
            msgBox.baseShowToast("click~")
        }


    }



}