package com.example.todo.a.viewholder

import android.graphics.Paint
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
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

    lateinit var listener : onChekBoxListener

    interface onChekBoxListener {
        fun checkChange(isCheck : Boolean ,  position : Int)
    }


    fun bindItem(todo: Todo, vm : AViewModel , listener : onChekBoxListener ) {

        binding.vo = todo

        binding.run {

            listCheckBox.isChecked = todo.isCheck.let {
                textStrike(it, text1)
                it
            }

            listCheckBox.setOnClickListener {
                listener.checkChange(
                    (it as CheckBox).isChecked
                    , adapterPosition )
            }

            executePendingBindings()
        }

    }

    private fun textStrike(isCheck: Boolean, text : TextView) {
        if (isCheck) text.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        else text.paintFlags = 0

    }


}