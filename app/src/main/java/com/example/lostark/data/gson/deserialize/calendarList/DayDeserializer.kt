package com.example.lostark.data.gson.deserialize.calendarList

import com.example.lostark.data.model.calendar.Day
import com.example.lostark.data.model.calendar.GearScore
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class DayDeserializer : JsonDeserializer<Day> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Day {
        val result = Day()
        val jsonObject = json?.asJsonObject

        if (jsonObject != null) {
            for ((key, value) in jsonObject.entrySet()) {
                val gearScore: GearScore? = context?.deserialize(value, GearScore::class.java)
                if (gearScore != null) {
                    gearScore.value = key.toInt()
                    result.addGearScore(gearScore)
                }
            }
        }
        return result
    }
}