package com.example.lostark.data.gson.deserialize.categoryList

import com.example.lostark.data.model.calendar.Category
import com.example.lostark.data.model.calendar.EventCalendar
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class CategoryListDeserializer : JsonDeserializer<EventCalendar> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): EventCalendar {
        val result = EventCalendar()
        val jsonArray = json?.asJsonArray
        val jsonCategoriesArray = jsonArray?.get(0)?.asJsonArray
        var index = 0
        jsonCategoriesArray?.forEach {
            result.addCalendar(
                Category(
                    index.toString(),
                    it?.asJsonArray?.get(0).toString(),
                    it?.asJsonArray?.get(1).toString(),
                )
            )
            index += 1
        }
        return result
    }
}