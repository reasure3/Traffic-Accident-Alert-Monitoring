package com.swengineering.team1.traffic_accident.model.remote_config

import java.util.Date

data class ConfigChangeHistory(
    val timestamp: Date,
    val adminUser: String,
    val changes: Map<String, Any>?
) 