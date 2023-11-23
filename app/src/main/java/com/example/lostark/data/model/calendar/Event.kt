package com.example.lostark.data.model.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    val code: String? = "",
    val imageName: String? = "",
    var name: String? = "",
    var times: MutableList<String> = mutableListOf(),
    var gearScore: Int? = 0,
    var category: String = "",
) : Parcelable {

}