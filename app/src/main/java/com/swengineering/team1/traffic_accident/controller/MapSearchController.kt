package com.swengineering.team1.traffic_accident.controller

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.Locale
import com.swengineering.team1.traffic_accident.model.MapLocationModel

object MapSearchController {
    fun searchLocation(context: Context, query: String): LatLng? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val result = geocoder.getFromLocationName(query, 1)
            if (result != null && result.isNotEmpty()) {
                LatLng(result[0].latitude, result[0].longitude)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun selectLocation(newLocation: LatLng) {
        MapLocationModel.selectedLocation.value = newLocation
    }
}