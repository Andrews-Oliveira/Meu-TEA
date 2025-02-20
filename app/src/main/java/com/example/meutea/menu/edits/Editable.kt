package com.example.meutea.menu.edits
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier



import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.ui.text.input.KeyboardType


@Composable
fun EditableTextFieldRow(label: String, value: String, onValueChange: (String) -> Unit, isNumeric: Boolean = false, maxLength: Int? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(0.3f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = {
                var filteredInput = if (isNumeric) {
                    it.filter { char -> char.isDigit() }
                } else {
                    it
                }

                if (maxLength != null) {
                    filteredInput = filteredInput.take(maxLength)
                }

                onValueChange(filteredInput)
            },
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
        )
    }
}