package com.swengineering.team1.traffic_accident.screen

import android.Manifest
import androidx.annotation.RequiresPermission
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.swengineering.team1.traffic_accident.controller.MapSearchController
import com.swengineering.team1.traffic_accident.controller.LocationController
import com.swengineering.team1.traffic_accident.model.MapLocationModel
import com.swengineering.team1.traffic_accident.screen.component.SearchBar



@OptIn(ExperimentalPermissionsApi::class)
@Preview
@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION]
)
@Composable
fun HotspotScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var showPermissionDialog by remember {
        mutableStateOf(true)
    }
    val permissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val permissionState = rememberMultiplePermissionsState(permissions = permissions)
    // 모든 권한이 승인되었는지 판단
    val allRequiredPermission =
        permissionState.revokedPermissions.none { it.permission in permissions }

    if (allRequiredPermission) {
        Column(modifier = modifier.fillMaxSize()) {
            SearchBar { query ->
                val latLng = MapSearchController.searchLocation(context, query)
                latLng?.let {
                    MapSearchController.selectLocation(it)
                }
            }
            ShowMap(Modifier.weight(1f))
        }
    } else if (showPermissionDialog) {
        PermissionDialog(permissionState) {
            showPermissionDialog = it
        }
    } else {
        Text("이미 권한이 거부되었습니다. 설정에서 직접 권한 설정을 해주세요", modifier)
    }
}

// API 키를 안주면 빈지도로 나옴
// API 키 설정 필수
@SuppressLint("MissingPermission", "UnrememberedMutableState")
@Composable
fun ShowMap(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val initialLatLng by MapLocationModel.initialLocation
    val selectedLatLng by MapLocationModel.selectedLocation
    val cameraPositionState = rememberCameraPositionState()

    // 최초 실행 시 현재 위치로 초기화
    LaunchedEffect(Unit) {
        if (initialLatLng == null) {
            val current = LocationController.getCurrentLocation(context)
            current?.let {
                MapLocationModel.initialLocation.value = it
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(it, 17f)
                )
            }
        }
    }

    // 선택된 위치가 있을 경우 카메라 이동
    LaunchedEffect(selectedLatLng) {
        selectedLatLng?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 17f),
                durationMs = 1000
            )
        }
    }

    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = true)               // 지도 우측 상단에 현재 위치 버튼 표시
    }
    val properties by remember {
        mutableStateOf(MapProperties(isMyLocationEnabled = true))   // 현재 위치에 파란 점 표시
    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ) {
        // 검색으로 선택된 위치만 마커로 표시
        selectedLatLng?.let {
            Marker(
                state = MarkerState(position = it),
                title = "선택한 위치"
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionDialog(
    permissionState: MultiplePermissionsState,
    showPermissionDialog: (Boolean) -> Unit
){
    AlertDialog(
        onDismissRequest = { showPermissionDialog(false) },
        title = {
            Text(text = "위치정보 권한 요청")
        },
        text = {
            Text(text = "PlacePick 서비스를 원활하게 이용하기 위해 위치를 설정해보세요!")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    showPermissionDialog(false)
                    permissionState.launchMultiplePermissionRequest() // 위치권한 요청
                }
            ) {
                Text(text = "확인")
            }
        },
        dismissButton = {
            TextButton(onClick = { showPermissionDialog(false) }) {
                Text(text = "취소")
            }
        }
    )
}