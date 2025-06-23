package com.swengineering.team1.traffic_accident.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh

@Composable
fun RefreshButtonView(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .padding(bottom = 88.dp, start = 24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "새로 고침"
        )
    }
}
