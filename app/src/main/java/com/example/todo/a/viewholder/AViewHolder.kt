package com.example.todo.a.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.common.Defines
import com.example.todo.databinding.ItemAFragmentBinding
import com.example.todo.model.domain.Todo

class AViewHolder (
    private val binding : ItemAFragmentBinding

) : RecyclerView.ViewHolder(
    binding.root
) {

    fun bindItem(todo: Todo) {
        binding.vo = todo
        binding.executePendingBindings()

    }

}