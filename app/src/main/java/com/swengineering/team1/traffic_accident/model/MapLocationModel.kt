package com.swengineering.team1.traffic_accident.model

import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.model.LatLng
import java.util.*

object MapLocationModel {
    val initialLocation = mutableStateOf<LatLng?>(null)
    val selectedLocation = mutableStateOf<LatLng?>(null)
}