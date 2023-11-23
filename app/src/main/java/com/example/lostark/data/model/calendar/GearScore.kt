package com.example.lostark.data.model.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GearScore(
    var value: Int = 0,
    val eventList: MutableList<Event> = mutableListOf(),
) : Parcelable {

    fun addEvent (event: Event) {
        this.eventList.add(event)
    }

}