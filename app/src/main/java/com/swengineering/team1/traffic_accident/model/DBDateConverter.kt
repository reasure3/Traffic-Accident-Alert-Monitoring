package com.swengineering.team1.traffic_accident.model

import androidx.room.TypeConverter
import java.util.Date

class DBDateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}