package com.swengineering.team1.traffic_accident.model.remote_config

data class NotificationConfig(
    val alertRadiusMeters: Int,
    val queryDistanceIntervalMeters: Int,
    val accidentPeriodDays: Int,
    val minAccidentCount: Int,
    val weatherConditionEnabled: Boolean,
    val notificationMinCooldownMills: Int,
    val notificationMaxCooldownMills: Int,
    val minSeverity: Int,
)