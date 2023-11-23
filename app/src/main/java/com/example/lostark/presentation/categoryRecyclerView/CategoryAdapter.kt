package com.example.lostark.presentation.categoryRecyclerView

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.lostark.data.model.calendar.Category
import com.example.lostark.data.model.calendar.Event

class CategoryAdapter : RecyclerView.Adapter<CategoryViewHolder>() {
    companion object {
        private const val CATEGORY_ITEM = 0
    }

    private val items = mutableListOf<Category>()

    lateinit var onItemClick: (View, ImageView) -> Unit
    lateinit var onNestedItemClick: (Category, Event) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return if (viewType == CATEGORY_ITEM) CategoryViewHolder(parent, onItemClick, onNestedItemClick)
        else throw Exception("unsupported view type")
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return CATEGORY_ITEM
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Category>) {
        this.items.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

}