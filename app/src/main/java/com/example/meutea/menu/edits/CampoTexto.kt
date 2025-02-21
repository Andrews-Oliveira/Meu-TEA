package com.example.meutea.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

// 🔹 Campo de Texto Formatado (CPF, RG, Data, Telefone)
@Composable
fun CustomFormattedTextField(
    label: String,
    value: String,
    mask: String,
    maxLength: Int,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = applyMask(value, mask),
        onValueChange = { newValue ->
            val unmaskedValue = newValue.filter { it.isDigit() }
            if (unmaskedValue.length <= maxLength) {
                onValueChange(unmaskedValue)
            }
        },
        label = { Text(label, color = Color.Black) }, // Mantém a cor preta
        placeholder = { Text(mask, color = Color.Gray) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.Black
        )
    )
}

// 🔹 Função de Aplicar Máscara
fun applyMask(input: String, mask: String): String {
    if (input.isEmpty()) return ""

    val cleanInput = input.filter { it.isDigit() }
    val formattedText = StringBuilder()
    var index = 0

    for (char in mask) {
        if (char == '#') {
            if (index < cleanInput.length) {
                formattedText.append(cleanInput[index])
                index++
            } else {
                break
            }
        } else {
            formattedText.append(char)
        }
    }

    return formattedText.toString()
}
@Composable
fun CustomTextField(label: String, value: String, maxLength: Int, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= maxLength) {
                onValueChange(newValue)
            }
        },
        label = { Text(label, color = Color.Black) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.Black
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuField(
    label: String,
    selected: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .clickable { expanded = true } // ✅ Pode clicar em qualquer lugar
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth() // ✅ Garante que não fique muito largo
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}



