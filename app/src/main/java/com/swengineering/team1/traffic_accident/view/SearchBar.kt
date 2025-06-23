package com.swengineering.team1.traffic_accident.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun SearchBar(
    searchText: TextFieldValue,
    onSearchTextChanged: (TextFieldValue) -> Unit,
    onSearchTriggered: () -> Unit,
    onClearSearch: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    BackHandler(enabled = searchText.text.isNotBlank()) {
        focusManager.clearFocus()      // 키보드 숨기기
        onClearSearch()
    }

    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        label = { Text("장소를 검색하세요") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                onSearchTriggered()
                focusManager.clearFocus()
            }
        ),
        trailingIcon = {
            TextButton(onClick = {
                onSearchTriggered()
                focusManager.clearFocus()
            }) {
                Text("검색")
            }
        }
    )
}