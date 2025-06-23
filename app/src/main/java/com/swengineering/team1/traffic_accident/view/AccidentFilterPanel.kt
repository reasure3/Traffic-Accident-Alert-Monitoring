package com.swengineering.team1.traffic_accident.view

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.swengineering.team1.traffic_accident.R

@Composable
fun AccidentFilterPanel(onFilterClick: () -> Unit) {
    Button(onClick = onFilterClick) {
        Text(stringResource(R.string.btn_filter))
    }
}
