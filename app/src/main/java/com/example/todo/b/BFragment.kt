package com.example.todo.b


import android.content.Context
import android.os.Bundle

import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.navigation.Navigation

import com.example.todo.R
import com.example.todo.base.BaseFragment
import com.example.todo.common.Defines
import com.example.todo.databinding.FragmentBBinding
import com.example.todo.vm.AViewModel
import com.example.todo.vm.eventObserve
import kotlinx.android.synthetic.main.fragment_b.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class BFragment : BaseFragment<FragmentBBinding, AViewModel>(
    R.layout.fragment_b
) {

    override val viewModel: AViewModel by sharedViewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Defines.log("viewModel DataValue -> ${viewModel.onClickButton.value}")
        this.setUpObserver()
    }


    override fun onResume() {
        super.onResume()
        Defines.log("onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Defines.log("onDestroy")
    }

    override fun onDestroyView() {
        super.keyboardHide(requireContext() , msg)
        super.onDestroyView()
        Defines.log("onDestroyView")
    }

    private fun setUpObserver() {
        viewModel.openEvent.eventObserve(viewLifecycleOwner) {

            super.back()
        }
    }








}