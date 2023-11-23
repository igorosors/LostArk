package com.example.lostark.presentation.datePickerRecyclerView

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lostark.data.model.calendar.Day

class DayAdapter : RecyclerView.Adapter<DayViewHolder>() {
    companion object {
        private const val DAY_ITEM = 0
    }

    private val items = mutableListOf<Day>()

    lateinit var onItemClick: (Day, Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        return if (viewType == DAY_ITEM) DayViewHolder(parent, onItemClick)
        else throw Exception("unsupported view type")


    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return DAY_ITEM
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Day>) {
        this.items.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

}