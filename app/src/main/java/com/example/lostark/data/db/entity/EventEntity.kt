package com.example.lostark.data.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lostark.data.db.dao.CalendarDao
import kotlinx.parcelize.Parcelize

@Entity(tableName = CalendarDao.EVENT_NAME)
@Parcelize
data class EventEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "time") var time: String?,
    @ColumnInfo(name = "gearScore") var gearScore: Int?,
    @ColumnInfo(name = "day") var day: Int?,
    @ColumnInfo(name = "month") var month: Int?,
) : Parcelable