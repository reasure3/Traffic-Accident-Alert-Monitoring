package com.swengineering.team1.traffic_accident.screen

import android.Manifest
import androidx.annotation.RequiresPermission
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

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
    // 설정한 permission List 중 첫번째 권한이 현재 포함되어 있는지
    val allRequiredPermission =
        permissionState.revokedPermissions.none { it.permission in permissions.first() }

    if (allRequiredPermission) {
        ShowMap(modifier)
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
@Composable
fun ShowMap(modifier: Modifier = Modifier) {
    val latLng = LatLng(0.0, 0.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 17f)
    }
    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = true)
    }
    val properties by remember {
        mutableStateOf(MapProperties(isMyLocationEnabled = true))
    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ) {
        // code
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