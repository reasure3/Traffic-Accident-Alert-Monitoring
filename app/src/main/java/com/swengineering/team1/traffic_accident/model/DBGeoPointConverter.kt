package com.swengineering.team1.traffic_accident.model

import androidx.room.TypeConverter
import com.google.firebase.firestore.GeoPoint

class DBGeoPointConverter {
    @TypeConverter
    fun fromGeoPoint(point: GeoPoint?): String? {
        return point?.let { "${it.latitude},${it.longitude}" }
    }

    @TypeConverter
    fun toGeoPoint(value: String?): GeoPoint? {
        return value?.split(",")?.let {
            if (it.size == 2) GeoPoint(it[0].toDouble(), it[1].toDouble()) else null
        }
    }
}