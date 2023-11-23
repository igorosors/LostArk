package com.example.lostark.presentation.eventRecyclerView

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lostark.data.model.calendar.Event

class EventAdapter : RecyclerView.Adapter<EventViewHolder>() {
    companion object {
        private const val EVENT_ITEM = 0
    }

    private val items = mutableListOf<Event>()

    lateinit var onItemClick: (Event) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return if (viewType == EVENT_ITEM) EventViewHolder(parent, onItemClick)
        else throw Exception("unsupported view type")
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return EVENT_ITEM
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Event>) {
        this.items.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

}