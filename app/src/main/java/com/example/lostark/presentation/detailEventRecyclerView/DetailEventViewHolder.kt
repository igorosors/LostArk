package com.example.lostark.presentation.detailEventRecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.lostark.R
import com.example.lostark.data.db.entity.EventEntity
import com.example.lostark.databinding.DetailEventItemBinding

class DetailEventViewHolder(
    parent: ViewGroup,
    private val onItemClick: (EventEntity) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.detail_event_item, parent, false)
) {
    private val binding by viewBinding(DetailEventItemBinding::bind)

    fun bind(eventEntity: EventEntity) {
        itemView.setOnClickListener {
            onItemClick(eventEntity)
        }
        binding.titleTextView.text = eventEntity.name
        binding.gearScoreTextView.text = eventEntity.gearScore.toString()
        binding.timeTextView.text = eventEntity.time
        val dayString = if (eventEntity.day.toString().length == 1) {
            "0" + eventEntity.day.toString()
        } else {
            eventEntity.day.toString()
        }
        val monthString = if (eventEntity.month.toString().length == 1) {
            "0" + eventEntity.month.toString()
        } else {
            eventEntity.day.toString()
        }
        binding.dateTextView.text = StringBuilder().append(dayString).append(".").append(monthString).toString()
    }
}