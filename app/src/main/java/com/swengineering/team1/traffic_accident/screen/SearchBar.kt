package com.swengineering.team1.traffic_accident.screen.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("장소를 검색하세요") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
        trailingIcon = {
            androidx.compose.material3.TextButton(onClick = {
                onSearch(text)
            }) {
                Text("검색")
            }
        }
    )
}