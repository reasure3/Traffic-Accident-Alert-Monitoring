package com.swengineering.team1.traffic_accident.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.swengineering.team1.traffic_accident.model.notification.NotificationLogDao
import com.swengineering.team1.traffic_accident.model.notification.NotificationLogItem

@Database(entities = [NotificationLogItem::class], version = 1)
@TypeConverters(DBDateConverter::class, DBGeoPointConverter::class)
abstract class AppLocalDatabase : RoomDatabase() {
    abstract fun notificationLogDao(): NotificationLogDao

    companion object {
        @Volatile private var INSTANCE: AppLocalDatabase? = null

        fun getInstance(context: Context): AppLocalDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppLocalDatabase::class.java,
                    "app_db"
                ).build().also { INSTANCE = it }
            }
    }
}