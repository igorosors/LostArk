package com.example.lostark.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lostark.data.db.entity.DayEntity
import com.example.lostark.data.db.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {
    companion object {
        const val CALENDAR_NAME = "calendar"
        const val EVENT_NAME = "event"
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCalendar(vararg calendarEntity: DayEntity)

    @Query("select * from $CALENDAR_NAME")
    fun getCalendarFlow(): Flow<DayEntity>

    @Query("delete from $CALENDAR_NAME")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveEvent(vararg eventEntity: EventEntity)

    @Query("select * from $EVENT_NAME")
    fun getEventFlow(): Flow<List<EventEntity>>

    @Query("delete from $EVENT_NAME where id = :id")
    suspend fun deleteEventById(id: Int)

    @Delete
    suspend fun deleteEvent(EventEntity: EventEntity)

}