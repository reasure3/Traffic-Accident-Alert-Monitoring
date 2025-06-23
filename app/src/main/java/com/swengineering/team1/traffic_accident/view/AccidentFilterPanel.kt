package com.swengineering.team1.traffic_accident.view

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AccidentFilterPanel(onFilterClick: () -> Unit) {
    Button(onClick = onFilterClick) {
        Text("필터")
    }
}
