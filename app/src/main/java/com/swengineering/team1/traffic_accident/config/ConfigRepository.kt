package com.swengineering.team1.traffic_accident.config

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.swengineering.team1.traffic_accident.R
import com.swengineering.team1.traffic_accident.models.NotificationConfig

class ConfigRepository private constructor() {
    private val remoteConfig = Firebase.remoteConfig.apply {
        setDefaultsAsync(R.xml.remote_config_defaults)
        setConfigSettingsAsync(remoteConfigSettings {
            // TODO: 배포 단계에서 간격 다시 조정하기
            minimumFetchIntervalInSeconds = 0 // To test remote config
        })
    }

    fun fetchRemoteConfig(
        onSuccess: () -> Unit = {},
        onFail: () -> Unit = {},
        onComplete: () -> Unit
    ) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFail()
                }
                onComplete()
            }
    }

    fun getNotificationConfig(): NotificationConfig {
        Log.d("remote config", "load config")
        return NotificationConfig(
            alertRadiusMeters =
                remoteConfig.getLong(ConfigConstants.ALERT_RADIUS_METERS).toInt(),
            queryDistanceIntervalMeters =
                remoteConfig.getLong(ConfigConstants.QUERY_DISTANCE_INTERVAL_METERS).toInt(),
            accidentPeriodDays =
                remoteConfig.getLong(ConfigConstants.ACCIDENT_PERIOD_DAYS).toInt(),
            minAccidentCount =
                remoteConfig.getLong(ConfigConstants.MIN_ACCIDENT_COUNT).toInt(),
            weatherConditionEnabled =
                remoteConfig.getBoolean(ConfigConstants.WEATHER_CONDITION_ENABLED),
            notificationMinCooldownMills =
                remoteConfig.getLong(ConfigConstants.NOTIFICATION_MIN_COOLDOWN_MILLS).toInt(),
            notificationMaxCooldownMills =
                remoteConfig.getLong(ConfigConstants.NOTIFICATION_MAX_COOLDOWN_MILLS).toInt(),
            minSeverity =
                remoteConfig.getLong(ConfigConstants.MIN_SEVERITY).toInt(),
        )
    }

    companion object {
        private var _instance: ConfigRepository? = null
        val INSTANCE: ConfigRepository
            get() {
                if (_instance == null) {
                    _instance = ConfigRepository()
                }
                return _instance!!
            }
    }
} 