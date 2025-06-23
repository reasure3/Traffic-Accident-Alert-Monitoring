package com.swengineering.team1.traffic_accident.view

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ShowGPSDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("GPS 비활성화") },
        text = { Text("GPS 기능이 꺼져 있습니다. 설정에서 활성화하시겠습니까?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("설정으로 이동")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}