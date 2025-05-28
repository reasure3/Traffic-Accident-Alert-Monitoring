package com.swengineering.team1.traffic_accident.controller

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import java.util.Locale
import com.swengineering.team1.traffic_accident.model.MapLocationModel
import kotlinx.coroutines.suspendCancellableCoroutine

object MapSearchController {

    suspend fun searchLocation(context: Context, query: String): LatLng? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            searchLocationWithGeocodeListener(context, query) // API 33 이상
        } else {
            searchLocationLegacy(context, query)              // API 32 이하
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun searchLocationWithGeocodeListener(context: Context, query: String): LatLng? {
        return suspendCancellableCoroutine { continuation ->
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                geocoder.getFromLocationName(
                    query,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(results: MutableList<Address>) {
                            if (results.isNotEmpty()) {
                                val address = results[0]
                                continuation.resume(
                                    LatLng(address.latitude, address.longitude),
                                    onCancellation = null
                                )
                            } else {
                                continuation.resume(null, onCancellation = null)
                            }
                        }

                        override fun onError(errorMessage: String?) {
                            continuation.resume(null, onCancellation = null)
                        }
                    }
                )
            } catch (e: Exception) {
                continuation.resume(null, onCancellation = null)
            }
        }
    }

    private suspend fun searchLocationLegacy(context: Context, query: String): LatLng? {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val result = geocoder.getFromLocationName(query, 1)
                if (!result.isNullOrEmpty()) {
                    LatLng(result[0].latitude, result[0].longitude)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    fun selectLocation(newLocation: LatLng) {
        MapLocationModel.selectedLocation.value = newLocation
    }

    fun clearSelectedLocation() {
        MapLocationModel.selectedLocation.value = null
    }
}