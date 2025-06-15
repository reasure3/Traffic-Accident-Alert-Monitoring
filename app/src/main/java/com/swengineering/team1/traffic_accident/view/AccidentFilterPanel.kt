package com.swengineering.team1.traffic_accident.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AccidentFilterPanel(onFilterClick: () -> Unit) {
    Button(onClick = onFilterClick) {
        Text("필터")
    }
}
