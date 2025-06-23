package com.swengineering.team1.traffic_accident.model.hotspot

data class AccidentItem(
    val id: String,
    val severity: Int,
    val weather: String,
    val latitude: String,
    val longitude: String
)