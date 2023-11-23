package com.example.lostark.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lostark.data.db.dao.CalendarDao
import com.example.lostark.data.db.entity.DayEntity
import com.example.lostark.data.db.entity.EventEntity

@Database(
    entities = [
        DayEntity::class,
        EventEntity::class,
    ],
    version = AppDatabase.DATABASE_VERSION
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "app_database"
    }

    abstract fun calendarDao(): CalendarDao
}