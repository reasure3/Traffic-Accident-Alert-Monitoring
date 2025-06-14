package com.swengineering.team1.traffic_accident.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.swengineering.team1.traffic_accident.screen.util.PermissionAwareScreen

@Preview
@Composable
fun NotificationScreen(modifier: Modifier = Modifier) {
    PermissionAwareScreen(
        onPermissionsGranted = {
            Text("위치 권한이 활성화 되었습니다", modifier)
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
            else null
        ).toTypedArray(),
        msgToSetting = "위치 권한을 항상 허용으로 바꿔주세요."
    )
}

