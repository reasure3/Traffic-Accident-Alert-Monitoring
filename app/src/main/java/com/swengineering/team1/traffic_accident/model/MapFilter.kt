package com.swengineering.team1.traffic_accident.model

data class MapFilter(
    val severityList: Set<Int> = emptySet(),
    val weatherList: Set<String> = emptySet()
)