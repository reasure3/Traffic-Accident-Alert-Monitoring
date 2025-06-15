package com.swengineering.team1.traffic_accident.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.swengineering.team1.traffic_accident.models.NotificationConfig

class ConfigRepository {

    private val remoteConfig = Firebase.remoteConfig

    fun getNotificationConfig(): NotificationConfig {
        return NotificationConfig(
            alertRadiusMeters = remoteConfig.getLong(ConfigConstants.ALERT_RADIUS_METERS).toInt(),
            accidentPeriodDays = remoteConfig.getLong(ConfigConstants.ACCIDENT_PERIOD_DAYS).toInt(),
            minAccidentCount = remoteConfig.getLong(ConfigConstants.MIN_ACCIDENT_COUNT).toInt(),
            weatherConditionEnabled = remoteConfig.getBoolean(ConfigConstants.WEATHER_CONDITION_ENABLED),
            notificationCooldownMinutes = remoteConfig.getLong(ConfigConstants.NOTIFICATION_COOLDOWN_MINUTES).toInt()
        )
    }
} 