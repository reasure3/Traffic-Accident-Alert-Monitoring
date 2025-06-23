package com.swengineering.team1.traffic_accident.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firebase.geofire.GeoLocation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.rememberCameraPositionState
import com.swengineering.team1.traffic_accident.R
import com.swengineering.team1.traffic_accident.hotspot.LocationError
import com.swengineering.team1.traffic_accident.hotspot.LocationService
import com.swengineering.team1.traffic_accident.hotspot.MapSearchController
import com.swengineering.team1.traffic_accident.model.hotspot.AccidentModel
import com.swengineering.team1.traffic_accident.model.hotspot.MapFilterModel
import com.swengineering.team1.traffic_accident.model.hotspot.MapLocationModel
import com.swengineering.team1.traffic_accident.view.AccidentFilterPanel
import com.swengineering.team1.traffic_accident.view.FilterDialog
import com.swengineering.team1.traffic_accident.view.PermissionAwareScreen
import com.swengineering.team1.traffic_accident.view.SearchBar
import com.swengineering.team1.traffic_accident.view.ShowGPSDialog
import com.swengineering.team1.traffic_accident.view.ShowMapView
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION]
)
@Composable
fun HotspotScreen(modifier: Modifier = Modifier) {
    PermissionAwareScreen(
        onPermissionsGranted = {
            InnerHotspotScreen()
        },
        onPermissionsDenied = { requestPermission ->
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.needs_permission_hotstpot))
                Spacer(Modifier.height(8.dp))
                Button(onClick = requestPermission) {
                    Text(stringResource(R.string.request_permission))
                }
            }
        },
        permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ),
        msgToSetting = stringResource(R.string.request_permission_msg_hotspot)
    )
}

@Composable
fun InnerHotspotScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val showFilterDialog = remember { mutableStateOf(false) }

    val selectedLatLng by MapLocationModel.selectedLocation.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    val showGpsDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!LocationService.isGpsEnabled(context)) {
            val defaultLocation = LocationService.getDefaultLocation()
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(defaultLocation, 17f)
            )
            MapLocationModel.setInitialLocation(defaultLocation)

            showGpsDialog.value = true
            return@LaunchedEffect
        }
        try {
            val location = LocationService.getCurrentLocation(context)
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(location, 17f)
            )
            MapLocationModel.setInitialLocation(location)
        } catch (_: LocationError.PermissionDenied) {
            Toast.makeText(context, R.string.no_permission_gps, Toast.LENGTH_SHORT).show()
        } catch (_: LocationError.GpsSignalWeak) {
            Toast.makeText(context, R.string.weak_gps, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(context, R.string.fail_retrieve_location, Toast.LENGTH_SHORT).show()
        }

    }

    if (showGpsDialog.value) {
        ShowGPSDialog(
            onDismiss = { showGpsDialog.value = false },
            onConfirm = {
                showGpsDialog.value = false
                openGpsSettings(context)
            }
        )
    }

//    LaunchedEffect(Unit) {
//        Log.d("HotspotScreen", "loadAccidents 호출됨")
//        AccidentModel.loadAccidents()
//    }
    /*
        LaunchedEffect(Unit) {
            Log.d("HotspotScreen", "testFirestoreAccess 호출됨")
            AccidentModel.testFirestoreAccess()
        }

     */
    // 선택된 위치가 있을 경우 카메라 이동
    LaunchedEffect(selectedLatLng) {
        selectedLatLng?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 17f),
                durationMs = 1000
            )
        }
    }

    val accidents by AccidentModel.accidentState.collectAsState()
    LaunchedEffect(accidents) {
        if (accidents.isNotEmpty()) {
            Log.d("HotspotScreen", "현재 사고 데이터 수: ${accidents.size}")
        }
    }

    val filterState by MapFilterModel.filterState.collectAsState()
    val filteredData = AccidentModel.getFilteredAccidents(
        allAccidents = accidents,
        severityList = filterState.severityList,
        weatherList = filterState.weatherList
    )
    /*
    val filtered = AccidentModel.getFilteredAccidents(
        allAccidents = allAccidents,
        severityList = filter.severityList,
        weatherList = filter.weatherList
    )
    */

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                SearchBarController(
                    onSearch = { query ->
                        coroutineScope.launch {
                            val latLng = MapSearchController.searchLocation(context, query)
                            latLng?.let { MapLocationModel.setSelectedLocation(it) }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(2.dp))
                AccidentFilterPanel(onFilterClick = { showFilterDialog.value = true })
            }
        },
        content = { innerPadding ->
            ShowMapView(
                cameraPositionState = cameraPositionState,
                accidents = filteredData,
                selectedLocation = selectedLatLng,
                onMyLocationClick = {
                    coroutineScope.launch {
                        if (!LocationService.isGpsEnabled(context)) {
                            showGpsDialog.value = true
                            return@launch
                        }

                        try {
                            val location = LocationService.getCurrentLocation(context)
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(location, 17f),
                                durationMs = 1000
                            )
                        } catch (_: LocationError.PermissionDenied) {
                            Toast.makeText(context, R.string.no_permission_gps, Toast.LENGTH_SHORT)
                                .show()
                        } catch (_: LocationError.GpsSignalWeak) {
                            Toast.makeText(context, R.string.weak_gps, Toast.LENGTH_SHORT).show()
                        } catch (_: Exception) {
                            Toast.makeText(
                                context,
                                R.string.fail_retrieve_location,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                onMapLoaded = {
                    val latLng = cameraPositionState.position.target
                    val loc = GeoLocation(latLng.latitude, latLng.longitude)
                    AccidentModel.loadAccidents(loc, cameraPositionState.position.zoom)
                },
                onRefreshClick = {
                    val latLng = cameraPositionState.position.target
                    val loc = GeoLocation(latLng.latitude, latLng.longitude)
                    AccidentModel.loadAccidents(loc, cameraPositionState.position.zoom)
                }
            )
        }
    )

    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.isMoving }
            .collect { isMoving ->
                if (!isMoving) {
                    val pos = cameraPositionState.position
                    Log.d("MapCamera", "Move Finished at ${pos.target}")
                    // 이동이 끝났을 때만 실행

                    val latLng = pos.target
                    val loc = GeoLocation(latLng.latitude, latLng.longitude)
                    AccidentModel.loadAccidents(loc, pos.zoom)
                }
            }
    }

    if (showFilterDialog.value) {
        FilterDialog(
            filter = filterState,
            onToggleSeverity = { MapFilterModel.toggleSeverity(it) },
            onToggleWeather = { MapFilterModel.toggleWeather(it) },
            onReset = { MapFilterModel.reset() },
            onDismiss = { showFilterDialog.value = false }
        )
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

fun openGpsSettings(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}