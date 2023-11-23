package com.example.lostark.presentation.eventRecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.lostark.R
import com.example.lostark.data.model.calendar.Event
import com.example.lostark.databinding.EventItemBinding

class EventViewHolder(
    parent: ViewGroup,
    private val onItemClick: (Event) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
) {
    private val binding by viewBinding(EventItemBinding::bind)

    fun bind(event: Event) {
        itemView.setOnClickListener {
            onItemClick(event)
        }
        binding.titleTextView.text = event.name
        binding.gearScoreTextView.text = event.gearScore.toString()
    }
}