package com.example.lostark.data.gson.deserialize.eventList

import com.example.lostark.data.model.calendar.Event
import com.example.lostark.data.model.calendar.GearScore
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class EventListDeserializer : JsonDeserializer<GearScore> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): GearScore {
        val result = GearScore()
        val jsonObject = json?.asJsonObject
        if (jsonObject != null) {
            for ((key, value) in jsonObject.entrySet()) {
                val eventList: MutableList<Any>? = context?.deserialize(value, mutableListOf<Any>()::class.java)
                result.addEvent(
                    Event(
                        code = key.toString(),
                        name = eventList?.getOrNull(0).toString(),
                        imageName = eventList?.getOrNull(1).toString(),
                        gearScore = eventList?.getOrNull(2).toString().toIntOrNull()
                    )
                )
            }
        }
        return result
    }
}