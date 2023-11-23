package com.example.lostark.presentation.timeRecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.lostark.R
import com.example.lostark.databinding.TimeItemBinding

class TimeViewHolder (
    parent: ViewGroup,
    private val onItemClick: (String) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.time_item, parent, false)
) {
    private val binding by viewBinding(TimeItemBinding::bind)

    fun bind(time: String) {
        itemView.setOnClickListener {
            onItemClick(time)
        }
        binding.timeTextView.text = time
    }

}