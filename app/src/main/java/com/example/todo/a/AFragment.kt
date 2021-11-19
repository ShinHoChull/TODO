package com.example.todo.a

import android.app.Application
import android.content.Intent
import android.os.Bundle

import android.view.*

import com.example.todo.base.BaseFragment

import com.example.todo.databinding.FragmentABinding
import com.example.todo.vm.AViewModel
import com.example.todo.R
import com.example.todo.a.service.MyService

import com.example.todo.common.Defines
import com.example.todo.model.domain.Todo
import kotlinx.android.synthetic.main.fragment_a.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AFragment : BaseFragment<FragmentABinding, AViewModel>(
    R.layout.fragment_a
) {

    override val viewModel: AViewModel by sharedViewModel()
    private lateinit var mAdapter: AdapterA


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        setUpVal()
        setUpObserver()
        setUpGPS()
    }

    /*
    등록된 JOB 이 있는지 확인하고 있으면 서비스 실행.
     */
    private fun setUpGPS() {
        GlobalScope.launch(Dispatchers.IO) {
            if (viewModel.getAllData().isNotEmpty()) {

                Defines.log("GPS를 실행합니다.")

                val intent = Intent(
                    requireContext()
                    , MyService::class.java).apply {
                    putExtra("flag", "start")
                }

                requireActivity().startService(intent)
            }
        }
    }

    private fun setUpVal() {

        AdapterA(ArrayList(), viewModel , requireActivity()).apply {
            list_view.adapter = this
            mAdapter = this
        }
    }

    override fun onResume() {
        super.onResume()

        GlobalScope.launch(Dispatchers.IO) {

            viewModel.getAllData().let {
                val arr = (it as ArrayList<Todo>).apply {
                    sortByDescending { it: Todo -> it.id }
                    viewModel.setTodoList(it)
                }

                mAdapter.setData(arr)
            }
        }
    }

    private fun setUpObserver() {

        viewModel.removeList.observe(viewLifecycleOwner) {
            for (row in it) {
                mAdapter.delData(row)
            }
        }
    }


}