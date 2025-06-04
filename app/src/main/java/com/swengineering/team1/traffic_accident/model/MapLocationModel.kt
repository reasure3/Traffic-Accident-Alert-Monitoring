package com.swengineering.team1.traffic_accident.model

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object MapLocationModel {

    private val _initialLocation = MutableStateFlow<LatLng?>(null)
    val initialLocation: StateFlow<LatLng?> = _initialLocation

    // 검색으로 선택한 위치
    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation

    // 현재 기기의 위치 (초기 로딩 시 설정됨)
    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation


    // 위치 설정 함수
    fun setInitialLocation(latLng: LatLng) {
        _initialLocation.value = latLng
    }
    fun setSelectedLocation(latLng: LatLng?) {
        _selectedLocation.value = latLng
    }

    fun setCurrentLocation(latLng: LatLng?) {
        _currentLocation.value = latLng
    }

    fun clearSelectedLocation() {
        _selectedLocation.value = null
    }

    fun isLocationInitialized(): Boolean {
        return _currentLocation.value != null
    }
}