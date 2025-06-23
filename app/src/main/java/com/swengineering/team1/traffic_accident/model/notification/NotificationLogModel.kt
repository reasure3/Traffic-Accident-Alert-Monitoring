package com.swengineering.team1.traffic_accident.model.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.swengineering.team1.traffic_accident.model.AppLocalDatabase
import kotlinx.coroutines.flow.Flow

class NotificationLogModel(application: Application): AndroidViewModel(application) {
    private val dao = AppLocalDatabase.getInstance(application).notificationLogDao()
    val logs: Flow<List<NotificationLogItem>> = dao.getLatestLogs()
}