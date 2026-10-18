package com.example.myappnavigation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.myappnavigation.ui.theme.VerdeCapebe

/**
 * Campo de formulario reutilizable: ícono a la izquierda, ícono de estado
 * (error o correcto) a la derecha y mensaje de error debajo.
 * No contiene reglas de validación: recibe el error ya calculado por el ViewModel.
 */
@Composable
fun CampoFormulario(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    icono: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    esPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = error != null,
        leadingIcon = { Icon(imageVector = icono, contentDescription = null) },
        trailingIcon = {
            if (error != null) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Campo con error",
                    tint = MaterialTheme.colorScheme.error
                )
            } else if (value.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Campo válido",
                    tint = VerdeCapebe
                )
            }
        },
        supportingText = {
            if (error != null) {
                Text(error)
            }
        },
        visualTransformation = if (esPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth()
    )
}
