package com.example.lostark.data.model.calendar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Day(
    var number: Int = 0,
    var monthNumber: Int = 0,
    var year: Int = 0,
    val gearScoreList: MutableList<GearScore> = mutableListOf(),
    var categoryList: MutableList<Category> = mutableListOf(),
    var name: String = "",
    var isPicked: Boolean = false,
    var nextYear: Boolean = false,
) : Parcelable {

    fun addGearScore (gearScore: GearScore) {
        this.gearScoreList.add(gearScore)
    }

    fun addCategory (category: Category) {
        this.categoryList.add(category)
    }

}