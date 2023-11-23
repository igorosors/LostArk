package com.example.lostark.data.gson.deserialize.calendarList

import com.example.lostark.data.model.calendar.Event
import com.example.lostark.data.model.calendar.GearScore
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class GearScoreDeserializer : JsonDeserializer<GearScore> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): GearScore {
        val result = GearScore()
        val jsonObject = json?.asJsonObject

        if (jsonObject != null) {
            for ((key, value) in jsonObject.entrySet()) {
                val eventList: MutableList<String>? = context?.deserialize(value, mutableListOf<String>()::class.java)
                if (eventList != null) {
                    result.addEvent(
                        Event(
                            code = key.toString(),
                            times = eventList
                        )
                    )
                }
            }
        }
        return result
    }
}