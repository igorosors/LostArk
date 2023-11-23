package com.example.lostark.presentation.categoryRecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.lostark.R
import com.example.lostark.data.model.calendar.Category
import com.example.lostark.data.model.calendar.Event
import com.example.lostark.databinding.CategoryItemBinding
import com.example.lostark.presentation.eventRecyclerView.EventAdapter

class CategoryViewHolder(
    parent: ViewGroup,
    private val onItemClick: (View, ImageView) -> Unit,
    private val onNestedItemClick: (Category, Event) -> Unit
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
) {
    private val binding by viewBinding(CategoryItemBinding::bind)

    fun bind(category: Category) {

        binding.categoryTextView.setOnClickListener {
            onItemClick(binding.eventLinearLayout, binding.imageArrow)
        }
        //binding.eventLinearLayout.visibility = View.GONE
        binding.eventLinearLayout.layoutParams.height = 1

        binding.categoryTextView.text = category.name
        val eventAdapter = EventAdapter()
        binding.eventRecyclerView.adapter = eventAdapter
        eventAdapter.setItems(category.eventList)
        eventAdapter.onItemClick = { event ->
            onNestedItemClick(category, event)
        }
        binding.imageArrow.rotation = 90.0f

    }
}
