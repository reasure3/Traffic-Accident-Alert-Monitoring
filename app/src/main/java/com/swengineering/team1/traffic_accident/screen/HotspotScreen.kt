package com.swengineering.team1.traffic_accident.screen

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.rememberCameraPositionState
import com.swengineering.team1.traffic_accident.model.AccidentModel
import com.swengineering.team1.traffic_accident.model.MapFilterModel
import com.swengineering.team1.traffic_accident.model.MapLocationModel
import com.swengineering.team1.traffic_accident.view.AccidentSummaryPanel
import com.swengineering.team1.traffic_accident.view.FilterDialog
import com.swengineering.team1.traffic_accident.view.SearchBar
import com.swengineering.team1.traffic_accident.screen.view.ShowMapView
import com.swengineering.team1.traffic_accident.service.LocationService
import com.swengineering.team1.traffic_accident.service.MapSearchService
import com.swengineering.team1.traffic_accident.view.PermissionDeniedView


@OptIn(ExperimentalPermissionsApi::class)
@Preview
@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION]
)
@Composable
fun HotspotScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showPermissionDialog by remember {
        mutableStateOf(true)
    }
    val permissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)
    val showFilterDialog = remember { mutableStateOf(false) }

    val selectedLatLng by MapLocationModel.selectedLocation.collectAsState()
    val cameraPositionState = rememberCameraPositionState()

    // 최초 실행 시 현재 위치로 초기화
    LaunchedEffect(Unit) {
        val location = LocationService.getCurrentLocation(context)
        val initLocation = location ?: LocationService.getDefaultLocation()

        MapLocationModel.setInitialLocation(initLocation)
        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(initLocation, 17f))
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

    val filterState by MapFilterModel.filterState.collectAsState()
    val filteredData = AccidentModel.getFilteredAccidents(
        severityList = filterState.severityList,
        weatherList = filterState.weatherList
    )

    // 모든 권한이 승인되었는지 판단
    val allRequiredPermission =
        permissionState.revokedPermissions.none { it.permission in permissions }

    if (allRequiredPermission) {
        Scaffold(
            topBar = {
                Column (modifier = Modifier.fillMaxSize().padding(32.dp)){
                    SearchBarController(
                        onSearch = { query ->
                            coroutineScope.launch {
                                val latLng = MapSearchService.searchLocation(context, query)
                                latLng?.let { MapLocationModel.setSelectedLocation(it) }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    AccidentSummaryPanel(onFilterClick = {showFilterDialog.value = true})
                }
            },
            content = { innerPadding ->
                ShowMapView(
                    cameraPositionState = cameraPositionState,
                    accidents = filteredData,
                    selectedLocation = selectedLatLng,
                    onMyLocationClick = {
                        coroutineScope.launch {
                            val location = LocationService.getCurrentLocation(context)
                                ?: LocationService.getDefaultLocation()

                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(location, 17f),
                                durationMs = 1000
                            )
                        }
                    }
                )
            }
        )

        if (showFilterDialog.value) {
            FilterDialog(
                filter = filterState,
                onToggleSeverity = { MapFilterModel.toggleSeverity(it) },
                onToggleWeather = { MapFilterModel.toggleWeather(it) },
                onReset = { MapFilterModel.reset() },
                onDismiss = { showFilterDialog.value = false }
            )
        }
    } else if (showPermissionDialog) {
        PermissionDeniedView(
            onPermissionRequest = {
                showPermissionDialog = false
                permissionState.launchMultiplePermissionRequest()
            },
            onDismiss = { showPermissionDialog = false }
        )
    } else {
        Text("이미 권한이 거부되었습니다. 설정에서 직접 권한 설정을 해주세요", modifier)
    }
}

@Composable
fun SearchBarController(
    onSearch: (String) -> Unit
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    SearchBar(
        searchText = searchText,
        onSearchTextChanged = { searchText = it },
        onSearchTriggered = {
            onSearch(searchText.text)
        },
        onClearSearch = {
            searchText = TextFieldValue("")
            MapLocationModel.clearSelectedLocation()
        }
    )
}
