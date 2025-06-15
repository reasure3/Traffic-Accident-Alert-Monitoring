package com.swengineering.team1.traffic_accident.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MyLocationButtonView(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .padding(bottom = 88.dp, end = 24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null
        )
    }
}