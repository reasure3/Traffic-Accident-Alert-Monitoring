package com.swengineering.team1.traffic_accident.models

data class NotificationConfig(
    val alertRadiusMeters: Int,
    val accidentPeriodDays: Int,
    val minAccidentCount: Int,
    val weatherConditionEnabled: Boolean,
    val notificationCooldownMinutes: Int
) 