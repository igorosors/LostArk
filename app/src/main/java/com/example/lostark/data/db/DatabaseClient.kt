package com.example.lostark.data.db

import android.content.Context
import androidx.room.Room
import com.example.lostark.data.db.entity.DayEntity
import com.example.lostark.data.db.entity.EventEntity
import kotlinx.coroutines.flow.Flow

class DatabaseClient private constructor(
 context: Context,
) {
    companion object {
        private var instance: DatabaseClient? = null

        fun getInstance(context: Context): DatabaseClient {
            return instance ?: run {
                val newInstance = DatabaseClient(context)
                instance = newInstance
                newInstance
            }
        }
    }

    private val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME
    ).build()

    suspend fun saveCalendar(calendar: DayEntity) {
        db.calendarDao().saveCalendar(calendar)
    }

    fun getCalendarFlow(): Flow<DayEntity> {
        return db.calendarDao().getCalendarFlow()
    }

    suspend fun clear() {
        db.calendarDao().clear()
    }

    suspend fun saveEvent(events: List<EventEntity>) {
        db.calendarDao().saveEvent(*events.toTypedArray())
    }

    fun getEventFlow(): Flow<List<EventEntity>> {
        return db.calendarDao().getEventFlow()
    }

    suspend fun deleteEventById(id: Int) {
        db.calendarDao().deleteEventById(id)
    }

    suspend fun deleteEvent(eventEntity: EventEntity) {
        db.calendarDao().deleteEvent(eventEntity)
    }
}