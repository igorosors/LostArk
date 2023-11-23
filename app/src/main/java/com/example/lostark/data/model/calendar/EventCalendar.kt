package com.example.lostark.data.model.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventCalendar(
    val categoryList: MutableList<Category> = mutableListOf(),
    // unused data from json
    val days: MutableList<String> = mutableListOf(),
) : Parcelable {

    fun addCalendar(calendar: Category) {
        this.categoryList.add(calendar)
    }

}