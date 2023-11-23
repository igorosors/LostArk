package com.example.lostark.data.model.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Category (
    var code: String? = "",
    val name: String? = "",
    val imageName: String? = "",
    val monthList: MutableList<Month> = mutableListOf(),
    val eventList: MutableList<Event> = mutableListOf(),
) : Parcelable {

    fun addMonth(month: Month) {
        this.monthList.add(month)
    }

    fun addEvent (event: Event) {
        this.eventList.add(event)
    }

}