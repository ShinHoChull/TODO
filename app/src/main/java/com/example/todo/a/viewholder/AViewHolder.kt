package com.example.todo.a.viewholder

import android.graphics.Paint
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.common.Defines
import com.example.todo.common.MsgBox
import com.example.todo.databinding.ItemAFragmentBinding
import com.example.todo.model.domain.Todo
import com.example.todo.vm.AViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AViewHolder (
    private val binding : ItemAFragmentBinding
) : RecyclerView.ViewHolder(
    binding.root
) {

    fun bindItem(todo: Todo, vm : AViewModel) {

        binding.vo = todo

        with(binding) {

            listCheckBox.setOnCheckedChangeListener { _, isChecked ->
                todo.isCheck = isChecked
                if (todo.isCheck) text1.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                else text1.paintFlags = 0

                vm.updateTodo(todo)
            }

            executePendingBindings()
        }
    }


}