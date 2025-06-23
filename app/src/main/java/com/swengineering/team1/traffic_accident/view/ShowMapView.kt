package com.swengineering.team1.traffic_accident.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.swengineering.team1.traffic_accident.R
import com.swengineering.team1.traffic_accident.model.hotspot.AccidentItem

@SuppressLint("MissingPermission")
@Composable
fun ShowMapView(
    cameraPositionState: CameraPositionState,
    accidents: List<AccidentItem>, // 컨트롤러에서 전달받은 필터된 데이터
    selectedLocation: LatLng?, // 컨트롤러에서 전달받은 현재 위치
    onMyLocationClick: () -> Unit,
    onMapLoaded: () -> Unit,
    onRefreshClick: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            ),
            properties = MapProperties(isMyLocationEnabled = true),
            onMapLoaded = onMapLoaded
        ) {
            selectedLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = stringResource(R.string.searched_marker_item)
                )
            }
            accidents.forEach { accident ->
                val lat = accident.latitude.toDoubleOrNull()
                val lng = accident.longitude.toDoubleOrNull()
                if (lat != null && lng != null) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(lat, lng)
                        ),
                        title = "${lat}, ${lng}",
                        snippet = stringResource(
                            R.string.accident_marker_item,
                            accident.severity,
                            stringResource(accident.weatherId)
                        )
                    )
                }
            }
        }

        RefreshButtonView(
            onClick = onRefreshClick,
            modifier = Modifier.align(Alignment.BottomStart)
        )

        MyLocationButtonView(
            onClick = onMyLocationClick,
            modifier = Modifier.align(Alignment.BottomEnd)
        )

    }

}
