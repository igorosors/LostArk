package com.example.lostark.presentation.detailEventRecyclerView

import com.example.lostark.presentation.eventRecyclerView.EventViewHolder

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lostark.data.db.entity.EventEntity
import com.example.lostark.data.model.calendar.Event

class DetailEventAdapter : RecyclerView.Adapter<DetailEventViewHolder>() {
    companion object {
        private const val EVENT_ITEM = 0
    }

    private val items = mutableListOf<EventEntity>()

    lateinit var onItemClick: (EventEntity) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailEventViewHolder {
        return if (viewType == EVENT_ITEM) DetailEventViewHolder(parent, onItemClick)
        else throw Exception("unsupported view type")
    }

    override fun onBindViewHolder(holder: DetailEventViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return EVENT_ITEM
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<EventEntity>) {
        this.items.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

}