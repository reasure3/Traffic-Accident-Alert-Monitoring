package com.swengineering.team1.traffic_accident.screen

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swengineering.team1.traffic_accident.R
import com.swengineering.team1.traffic_accident.model.notification.NotificationLogModel
import com.swengineering.team1.traffic_accident.service.LocationNotificationService
import com.swengineering.team1.traffic_accident.view.NotificationLogItemView
import com.swengineering.team1.traffic_accident.view.PermissionAwareScreen

@Composable
fun NotificationScreen(modifier: Modifier = Modifier) {
    PermissionAwareScreen(
        onPermissionsGranted = {
            LocationView(modifier.fillMaxSize())
        },
        onPermissionsDenied = { requestPermission ->
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.needs_permission_notification))
                Spacer(Modifier.height(8.dp))
                Button(onClick = requestPermission) {
                    Text(stringResource(R.string.request_permission))
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
        msgToSetting = stringResource(R.string.request_permission_msg_location_always)
    )
}

@Composable
fun LocationView(modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext as Application
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        LocationServiceController(Modifier.fillMaxWidth(), context)
        Spacer(Modifier.height(48.dp))
        NotificationLogView(Modifier.fillMaxSize(), context)
    }
}

@Composable
private fun LocationServiceController(modifier: Modifier = Modifier, context: Context) {
    val isRunning by LocationNotificationService.isRunning.collectAsState()

    // 서비스 인텐트
    val serviceIntent = remember {
        Intent(context, LocationNotificationService::class.java)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            val label = if (isRunning) stringResource(R.string.service_is_running)
            else stringResource(R.string.service_is_stop)
            Icon(
                imageVector = Icons.Filled.Circle,
                contentDescription = label,
                tint = if (isRunning) Color.Green else Color.Red
            )
            Spacer(Modifier.width(8.dp))
            Text(text = label)
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (isRunning) {
                    context.stopService(serviceIntent)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp)
        ) {
            val label = if (isRunning) stringResource(R.string.stop_service) else stringResource(R.string.start_service)
            Icon(
                imageVector = if (isRunning) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                contentDescription = label
            )
            Spacer(Modifier.width(8.dp))
            Text(text = label)
        }
    }
}

@Composable
private fun NotificationLogView(modifier: Modifier = Modifier, context: Application) {
    val viewModelFactory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificationLogModel(context) as T
            }
        }
    }

    val viewModel: NotificationLogModel = viewModel(factory = viewModelFactory)
    val logList by viewModel.logs.collectAsState(initial = emptyList())

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Filled.History,
                contentDescription = stringResource(R.string.recent_100_logs),
            )
            Spacer(Modifier.width(8.dp))
            Text(text = stringResource(R.string.recent_100_logs))
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(logList) { log ->
                NotificationLogItemView(log)
                HorizontalDivider()
            }
        }
    }
}
