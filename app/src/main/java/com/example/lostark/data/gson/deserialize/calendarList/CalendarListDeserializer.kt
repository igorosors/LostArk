package com.example.lostark.data.gson.deserialize.calendarList

import com.example.lostark.data.model.calendar.Category
import com.google.gson.JsonDeserializer
import com.example.lostark.data.model.calendar.EventCalendar
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import java.lang.reflect.Type

class CalendarListDeserializer : JsonDeserializer<EventCalendar> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): EventCalendar {
        val result = EventCalendar()
        val jsonObject = json?.asJsonObject

        if (jsonObject != null) {
            for ((key, value) in jsonObject.entrySet()) {
                val calendar: Category? = context?.deserialize(value, Category::class.java)
                if (calendar != null) {
                    calendar.code = key.toString()
                    result.addCalendar(calendar)
                }
            }
        }

        return result
    }
}