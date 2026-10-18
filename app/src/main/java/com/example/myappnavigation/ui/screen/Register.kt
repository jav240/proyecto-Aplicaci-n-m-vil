package com.example.myappnavigation.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myappnavigation.ui.components.CampoFormulario
import com.example.myappnavigation.ui.components.Footer
import com.example.myappnavigation.ui.components.Header
import com.example.myappnavigation.ui.theme.AzulCapebe
import com.example.myappnavigation.ui.theme.CieloCapebe
import com.example.myappnavigation.ui.theme.VerdeCapebe
import com.example.myappnavigation.ui.viewmodel.RegisterViewModel

@Composable
fun Register(
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val state = viewModel.uiState
    val context = LocalContext.current

    // El botón cambia de color con una animación cuando el formulario es válido
    val colorBoton by animateColorAsState(
        targetValue = if (viewModel.formularioValido) VerdeCapebe else AzulCapebe,
        label = "colorBotonRegistro"
    )

    Scaffold(
        topBar = { Header("REGISTRO DE SOCIO") },
        bottomBar = { Footer() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CieloCapebe)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Crea tu cuenta", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            CampoFormulario(
                value = state.name,
                onValueChange = { viewModel.onNameChanged(it) },
                label = "Nombre completo",
                error = state.nameError,
                icono = Icons.Default.Person
            )
            Spacer(modifier = Modifier.height(4.dp))

            CampoFormulario(
                value = state.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                label = "Correo electrónico",
                error = state.emailError,
                icono = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(4.dp))

            CampoFormulario(
                value = state.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                label = "Contraseña",
                error = state.passwordError,
                icono = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                esPassword = true
            )
            Spacer(modifier = Modifier.height(4.dp))

            CampoFormulario(
                value = state.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                label = "Repetir contraseña",
                error = state.confirmError,
                icono = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                esPassword = true
            )

            // Mensaje general, con animación de entrada y salida
            AnimatedVisibility(visible = state.errorMessage != null) {
                Text(
                    text = state.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.onRegisterClick(context) {
                        Toast.makeText(
                            context,
                            "¡Registro exitoso! Por favor inicia sesión.",
                            Toast.LENGTH_SHORT
                        ).show()
                        onNavigateToLogin()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Registrarse")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { onNavigateToLogin() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Ya tengo cuenta")
            }
        }
    }
}
