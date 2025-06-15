package com.swengineering.team1.traffic_accident.screen.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.swengineering.team1.traffic_accident.model.AccidentItem
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.Alignment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.graphics.Color as AndroidColor
import androidx.core.graphics.createBitmap
import com.swengineering.team1.traffic_accident.view.MyLocationButtonView

@SuppressLint("MissingPermission")
@Composable
fun ShowMapView(
    cameraPositionState: CameraPositionState,
    accidents: List<AccidentItem>, // 컨트롤러에서 전달받은 필터된 데이터
    selectedLocation: LatLng?, // 컨트롤러에서 전달받은 현재 위치
    onMyLocationClick: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false),
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            selectedLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "검색 위치"
                )
            }
            accidents.forEach { accident ->
                val lat = accident.latitude.toDoubleOrNull()
                val lng = accident.longitude.toDoubleOrNull()
                if (lat != null && lng != null) {
                    Marker(
                        state = MarkerState(position = LatLng(lat, lng)),
                        // icon = BitmapDescriptorFactory.fromBitmap(createRedDotBitmap(16)),
                        title = "${lat}, ${lng}"
                    )
                }
            }
        }
        MyLocationButtonView(
            onClick = onMyLocationClick,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }

}

// 빨간 점 비트맵 생성 함수
fun createRedDotBitmap(sizePx: Int = 16): Bitmap {
    val bitmap = createBitmap(sizePx, sizePx)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = AndroidColor.RED
        isAntiAlias = true
    }
    canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f, paint)
    return bitmap
}
