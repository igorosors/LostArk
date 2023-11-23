package com.example.lostark.presentation.timeRecyclerView

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lostark.data.model.calendar.Event

class TimeAdapter : RecyclerView.Adapter<TimeViewHolder>() {
    companion object {
        private const val TIME_ITEM = 0
    }

    private val items = mutableListOf<String>()

    lateinit var onItemClick: (String) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        return if (viewType == TIME_ITEM) TimeViewHolder(parent, onItemClick)
        else throw Exception("unsupported view type")
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return TIME_ITEM
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<String>) {
        this.items.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

}