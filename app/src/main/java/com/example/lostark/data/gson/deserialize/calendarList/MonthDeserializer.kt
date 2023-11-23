package com.example.lostark.data.gson.deserialize.calendarList

import com.example.lostark.data.model.calendar.Day
import com.example.lostark.data.model.calendar.Month
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class MonthDeserializer : JsonDeserializer<Month> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Month {
        val result = Month()
        val jsonObject = json?.asJsonObject

        if (jsonObject != null) {
            for ((key, value) in jsonObject.entrySet()) {
                val day: Day? = context?.deserialize(value, Day::class.java)
                if (day != null) {
                    day.number = key.toInt()
                    result.addDay(day)
                }
            }
        }
        return result
    }

}