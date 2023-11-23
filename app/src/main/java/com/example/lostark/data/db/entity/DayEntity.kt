package com.example.lostark.data.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lostark.data.db.dao.CalendarDao
import kotlinx.parcelize.Parcelize

@Entity(tableName = CalendarDao.CALENDAR_NAME)
@Parcelize
data class DayEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "dayList") var dayList: String?
) : Parcelable


