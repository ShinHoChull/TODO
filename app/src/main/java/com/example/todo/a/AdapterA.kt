package com.example.todo.a

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    RecyclerView.Adapter<AViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AViewHolder {

        val binding = ItemAFragmentBinding
            .inflate(LayoutInflater.from(parent.context)
                , parent
                , false)

        return AViewHolder(binding )
    }

    override fun onBindViewHolder(holder: AViewHolder, position: Int) {
        holder.bindItem(dataSet[position] , vm)
    }

    override fun getItemCount(): Int = dataSet.size


    fun setData(dataSet: ArrayList<Todo>) {
        if (dataSet.size <= 0) return
        this.dataSet = dataSet
        notifyDataSetChanged()
    }


}