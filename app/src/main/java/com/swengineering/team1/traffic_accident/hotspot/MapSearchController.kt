package com.swengineering.team1.traffic_accident.hotspot

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale

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
                    object : GeocodeListener {
                        override fun onGeocode(results: MutableList<Address>) {
                            if (results.isNotEmpty()) {
                                val address = results[0]
                                continuation.resume(
                                    LatLng(address.latitude, address.longitude)
                                ) { cause, _, _ -> }
                            } else {
                                continuation.resume(null) { cause, _, _ -> }
                            }
                        }

                        override fun onError(errorMessage: String?) {
                            continuation.resume(null) { cause, _, _ -> }
                        }
                    }
                )
            } catch (_: Exception) {
                continuation.resume(null) { cause, _, _ -> }
            }
        }
    }

    private suspend fun searchLocationLegacy(context: Context, query: String): LatLng? {
        return withContext(Dispatchers.IO) {
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
}