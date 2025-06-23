package com.swengineering.team1.traffic_accident.model.notification

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.GeoPoint
import java.util.Date

@Entity(tableName = "notification_logs")
data class NotificationLogItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Date = Date(),
    val location: GeoPoint? = null,
    val severity: Int = 0,
    val country: String = "",
    val state: String = "",
    val city: String = "",
    val street: String = "",
)
