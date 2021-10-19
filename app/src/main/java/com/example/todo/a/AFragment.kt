package com.example.todo.a

import android.app.Application
import android.os.Bundle

import android.view.*
import androidx.fragment.app.activityViewModels

import com.example.todo.base.BaseFragment

import com.example.todo.databinding.FragmentABinding
import com.example.todo.vm.AViewModel
import com.example.todo.R
import com.example.todo.base.BaseViewModel
import com.example.todo.common.Defines
import com.example.todo.common.MsgBox
import com.example.todo.model.domain.Todo
import kotlinx.android.synthetic.main.fragment_a.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AFragment : BaseFragment<FragmentABinding, AViewModel>(
    R.layout.fragment_a
) {

    override val viewModel: AViewModel by sharedViewModel()
    private lateinit var mAdapter : AdapterA

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        setUpVal()
        setUpObserver()

    }

    private fun setUpVal () {

        mAdapter = AdapterA(ArrayList(), viewModel).apply {
            list_view.adapter = this
        }
    }

    private fun setUpObserver() {

        viewModel.mTodoItems.observe(viewLifecycleOwner) {
            Defines.log("dataSet!!!!")
            val arr = it as ArrayList<Todo>
            arr.sortByDescending { it : Todo -> it.id }
            mAdapter.setData(arr)
        }

    }



}