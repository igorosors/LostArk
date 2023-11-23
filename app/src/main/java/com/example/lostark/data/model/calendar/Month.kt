package com.example.lostark.data.model.calendar;

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Month(
    var number: Int = 0,
    var dayList: MutableList<Day> = mutableListOf(),
) : Parcelable {

    fun addDay (day: Day) {
        this.dayList.add(day)
    }

}
