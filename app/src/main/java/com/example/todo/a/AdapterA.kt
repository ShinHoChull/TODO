package com.example.todo.a

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.a.viewholder.AViewHolder
import com.example.todo.common.Defines
import com.example.todo.common.getRandNum
import com.example.todo.databinding.ItemAFragmentBinding
import com.example.todo.model.domain.Todo
import com.example.todo.vm.AViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AdapterA(
    private var dataSet: ArrayList<Todo>
    , private val vm : AViewModel
    ) :
    RecyclerView.Adapter<AViewHolder>()
    , AViewHolder.onChekBoxListener {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AViewHolder {

        val binding = ItemAFragmentBinding
            .inflate(LayoutInflater.from(parent.context)
                , parent
                , false)

        return AViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AViewHolder, position: Int) {
        holder.bindItem(dataSet[position] , vm , this)
    }

    override fun getItemCount(): Int = dataSet.size


    fun setData(dataSet: ArrayList<Todo>) {
        if (dataSet.isEmpty()) return

        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    override fun checkChange(isCheck: Boolean, position: Int) {
        val row = this.dataSet[position]
        row.isCheck = isCheck

        //notifyItemChange 를 호출하면 오류가 나는 문제 발생.
        //백그라운드로 돌고있었는듯..?
        Handler(Looper.getMainLooper()).postDelayed({
            notifyItemChanged(position)
            vm.updateTodo(row)
        }, 100)

    }
}