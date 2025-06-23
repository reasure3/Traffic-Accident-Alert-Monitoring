package com.swengineering.team1.traffic_accident.model.hotspot

import androidx.annotation.StringRes

data class AccidentItem(
    val id: String,
    val severity: Int,
    val weather: String,
    @StringRes val weatherId: Int,
    val latitude: String,
    val longitude: String
)