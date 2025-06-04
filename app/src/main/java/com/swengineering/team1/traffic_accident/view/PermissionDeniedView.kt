package com.swengineering.team1.traffic_accident.view

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun PermissionDeniedView(onPermissionRequest: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("위치정보 권한 요청") },
        text = { Text("PlacePick 서비스를 원활하게 이용하기 위해 위치를 설정해보세요!") },
        confirmButton = {
            TextButton(onClick = onPermissionRequest) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}