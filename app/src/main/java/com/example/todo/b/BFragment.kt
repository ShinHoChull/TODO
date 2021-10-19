package com.example.todo.b


import android.os.Bundle

import android.view.*

import com.example.todo.R
import com.example.todo.base.BaseFragment
import com.example.todo.common.Defines
import com.example.todo.common.MsgBox
import com.example.todo.databinding.FragmentBBinding
import com.example.todo.extensions.eventObserve
import com.example.todo.vm.AViewModel
import kotlinx.android.synthetic.main.fragment_b.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class BFragment : BaseFragment<FragmentBBinding, AViewModel>(
    R.layout.fragment_b
) {

    override val viewModel: AViewModel by sharedViewModel()
    private val msgBox : MsgBox by inject() 


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Defines.log("viewModel DataValue -> ${viewModel.onClickButton.value}")
        this.setUpObserver()
    }


    override fun onDestroyView() {
        super.keyboardHide(requireContext() , msg)
        super.onDestroyView()
    }

    private fun setUpObserver() {

        viewModel.openEvent.eventObserve(viewLifecycleOwner) {
            super.keyboardHide(requireContext() , mBinding.msg)
            super.back()
        }

        viewModel.isTextCheck.observe(viewLifecycleOwner) {
            if (it) msgBox.baseShowToast("메세지를 입력해주세요.")
        }

    }

}