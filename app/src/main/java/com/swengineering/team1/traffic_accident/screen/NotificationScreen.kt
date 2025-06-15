package com.swengineering.team1.traffic_accident.screen

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swengineering.team1.traffic_accident.screen.util.PermissionAwareScreen
import com.swengineering.team1.traffic_accident.service.LocationService

@Preview
@Composable
fun NotificationScreen(modifier: Modifier = Modifier) {
    PermissionAwareScreen(
        onPermissionsGranted = {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LocationServiceController()
            }
        },
        onPermissionsDenied = { requestPermission ->
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("백그라운드 위치 권한이 필요합니다")
                Spacer(Modifier.height(8.dp))
                Button(onClick = requestPermission) {
                    Text("권한 요청하기")
                }
            }
        },
        permissions = listOfNotNull(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            else null,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.POST_NOTIFICATIONS
            else null
        ).toTypedArray(),
        msgToSetting = "위치 권한을 항상 허용으로 바꿔주세요."
    )
}

@Composable
fun LocationServiceController() {
    val context = LocalContext.current
    val isRunning by LocationService.isRunning.collectAsState()

    // 서비스 인텐트
    val serviceIntent = remember {
        Intent(context, LocationService::class.java)
    }

    // 버튼을 눌러 서비스 토글
    Button(onClick = {
        if (isRunning) {
            context.stopService(serviceIntent)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }) {
        val label = if (isRunning) {
            "서비스 중지"
        } else {
            "서비스 시작"
        }
        Text(text = label)
    }
}

