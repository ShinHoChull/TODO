package com.example.todo.b

import android.os.Bundle

import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.todo.R
import com.example.todo.base.BaseFragment
import com.example.todo.common.Defines
import com.example.todo.databinding.FragmentBBinding
import com.example.todo.vm.AViewModel
import kotlinx.android.synthetic.main.fragment_b.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

import org.koin.androidx.viewmodel.ext.android.viewModel

class BFragment : BaseFragment<FragmentBBinding, AViewModel>(
    R.layout.fragment_b
) {

    override val viewModel: AViewModel by sharedViewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpInject()


    }

    private fun setUpInject() {
        //종속성
        viewModel.mFragmentB = this

    }




}