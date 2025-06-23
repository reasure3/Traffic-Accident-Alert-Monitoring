package com.swengineering.team1.traffic_accident.model.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationLogDao {
    @Query("SELECT * FROM notification_logs ORDER BY timestamp DESC LIMIT 100")
    fun getLatestLogs(): Flow<List<NotificationLogItem>>

    @Insert
    suspend fun insert(log: NotificationLogItem)

    @Query("DELETE FROM notification_logs")
    suspend fun clear()
}