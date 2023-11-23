package com.example.lostark.presentation.datePickerRecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.lostark.R
import com.example.lostark.data.model.calendar.Day
import com.example.lostark.databinding.DayItemBinding

class DayViewHolder(
    parent: ViewGroup,
    private val onItemClick: (Day, Int) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false)
) {
    private val binding by viewBinding(DayItemBinding::bind)

    fun bind(day: Day, position: Int) {
        itemView.setOnClickListener {
            onItemClick(day, position)
        }
        binding.dayTextView.text = day.number.toString()
        binding.dayTextView.setTextColor(
            if (day.isPicked) ContextCompat.getColor(itemView.context, R.color.black)
            else ContextCompat.getColor(itemView.context, R.color.gray)
        )
    }

}