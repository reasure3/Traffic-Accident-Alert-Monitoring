package com.swengineering.team1.traffic_accident.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swengineering.team1.traffic_accident.model.hotspot.MapFilter

@Composable
fun FilterDialog(
    filter: MapFilter,
    onToggleSeverity: (Int) -> Unit,
    onToggleWeather: (String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("필터 설정") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("사고 심각도")
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..4).forEach { level ->
                        FilterChip(
                            selected = filter.severityList.contains(level),
                            onClick = {
                                onToggleSeverity(level)
                            },
                            label = { Text("$level") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("날씨")
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("맑음", "비", "눈", "흐림").forEach { weather ->
                        FilterChip(
                            selected = filter.weatherList.contains(weather),
                            onClick = {
                                onToggleWeather(weather)
                            },
                            label = { Text(weather) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onReset) {
                Text("초기화")
            }
        }
    )
}
