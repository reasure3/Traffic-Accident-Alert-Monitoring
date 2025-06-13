package com.swengineering.team1.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object RemoteConfigManager {

    private val remoteConfig = Firebase.remoteConfig

    fun init() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // Cache for 1 hour
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Set default values (ideally from a local XML or map)
        val defaults = mapOf(
            ConfigConstants.ALERT_RADIUS_METERS to 5000,
            ConfigConstants.ACCIDENT_PERIOD_DAYS to 30,
            ConfigConstants.MIN_ACCIDENT_COUNT to 5,
            ConfigConstants.WEATHER_CONDITION_ENABLED to false,
            ConfigConstants.NOTIFICATION_COOLDOWN_MINUTES to 60
        )
        remoteConfig.setDefaultsAsync(defaults)
    }

    fun fetchAndActivate(onComplete: (Boolean) -> Unit) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }
} 