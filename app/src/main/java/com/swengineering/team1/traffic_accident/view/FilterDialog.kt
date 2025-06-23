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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swengineering.team1.traffic_accident.R
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
        title = { Text(stringResource(R.string.title_filter)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.filter_severity_title))
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

                Text(stringResource(R.string.filter_weather_title))
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "fair" to stringResource(R.string.filter_weather_fair),
                        "rain" to stringResource(R.string.filter_weather_rain),
                        "snow" to stringResource(R.string.filter_weather_snow),
                        "cloudy" to stringResource(R.string.filter_weather_cloudy),
                    ).forEach { weather ->
                        FilterChip(
                            selected = filter.weatherList.contains(weather.first),
                            onClick = {
                                onToggleWeather(weather.first)
                            },
                            label = {
                                val text = weather.second

                                // 글자 길이에 따라 폰트 크기 조절 (예시: 글자 수 기준으로 줄이기)
                                val fontSize = when {
                                    text.length <= 4 -> 14.sp
                                    text.length <= 8 -> 10.sp
                                    else -> 6.sp
                                }

                                Text(
                                    text = text,
                                    fontSize = fontSize,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onReset) {
                Text(stringResource(R.string.bnt_reset))
            }
        }
    )
}
