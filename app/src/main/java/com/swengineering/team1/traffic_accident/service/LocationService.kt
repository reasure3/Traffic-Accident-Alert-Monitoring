package com.swengineering.team1.traffic_accident.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed class LocationError : Exception() {
    object GpsSignalWeak : LocationError()
    object PermissionDenied : LocationError()
    object GpsDisabled : LocationError()
}

object LocationService {

    @SuppressLint("ServiceCast")
    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    suspend fun getCurrentLocation(context: Context): LatLng {
        return suspendCancellableCoroutine { continuation ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            continuation.resume(LatLng(location.latitude, location.longitude))
                        } else {
                            continuation.resumeWithException(LocationError.GpsSignalWeak)
                        }
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWithException(e)
                    }
            } catch (e: SecurityException) {
                continuation.resumeWithException(LocationError.PermissionDenied)
            }
        }
    }

    fun getDefaultLocation(): LatLng = LatLng(37.5665, 126.9780) // 수정 예정
}