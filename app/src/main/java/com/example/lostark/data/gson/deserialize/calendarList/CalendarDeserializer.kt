package com.example.lostark.data.gson.deserialize.calendarList

import com.example.lostark.data.model.calendar.Category
import com.google.gson.JsonDeserializer
import com.example.lostark.data.model.calendar.Month
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import java.lang.reflect.Type

class CalendarDeserializer : JsonDeserializer<Category> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Category {
        val result = Category()
        val jsonObject = json?.asJsonObject

        if (jsonObject != null) {
            for ((key, value) in jsonObject.entrySet()) {
                val month: Month? = context?.deserialize(value, Month::class.java)
                if (month != null) {
                    month.number = key.toInt()
                    result.addMonth(month)
                }
            }
        }

        return result
    }
}