package com.swengineering.team1.traffic_accident.screen.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun SearchBar(onSearch: (String) -> Unit, onClearSearch: () -> Unit) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current

    BackHandler(enabled = text.text.isNotBlank()) {
        focusManager.clearFocus()      // 키보드 숨기기
        text = TextFieldValue("")      // 텍스트 초기화
        onClearSearch()
    }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("장소를 검색하세요") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                onSearch(text.text)
                focusManager.clearFocus()
            }
        ),
        trailingIcon = {
            TextButton(onClick = {
                onSearch(text.text)
            }) {
                Text("검색")
            }
        }
    )
}