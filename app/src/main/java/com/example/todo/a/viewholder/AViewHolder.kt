package com.example.todo.a.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.common.Defines
import com.example.todo.common.MsgBox
import com.example.todo.databinding.ItemAFragmentBinding
import com.example.todo.model.domain.Todo
import com.example.todo.vm.AViewModel
import org.koin.android.ext.android.inject


class AViewHolder (
    private val binding : ItemAFragmentBinding
) : RecyclerView.ViewHolder(
    binding.root
) {

    //private val msgBox : MsgBox by inject()

    fun bindItem(todo: Todo) {
        binding.vo = todo
        binding.listCheckBox.setOnCheckedChangeListener { _, isChecked ->
            Defines.log("check->$isChecked position->$adapterPosition")
        }

        binding.executePendingBindings()

    }

}