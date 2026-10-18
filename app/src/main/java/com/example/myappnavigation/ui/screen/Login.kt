package com.example.myappnavigation.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myappnavigation.ui.components.CampoFormulario
import com.example.myappnavigation.ui.components.Footer
import com.example.myappnavigation.ui.components.Header
import com.example.myappnavigation.ui.theme.CieloCapebe
import com.example.myappnavigation.ui.viewmodel.LoginViewModel

@Composable
fun Login(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val state = viewModel.uiState
    val context = LocalContext.current

    Scaffold(
        topBar = { Header("INGRESO DEL SOCIO") },
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
            Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            CampoFormulario(
                value = state.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                label = "Correo electrónico",
                error = state.emailError,
                icono = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(8.dp))

            CampoFormulario(
                value = state.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                label = "Contraseña",
                error = state.passwordError,
                icono = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                esPassword = true
            )

            // Mensaje de error general, con animación de entrada y salida
            AnimatedVisibility(visible = viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.onLoginClick(context) {
                        Toast.makeText(context, "¡Bienvenid@ de nuevo!", Toast.LENGTH_SHORT).show()
                        onLoginSuccess(viewModel.uiState.email.trim().lowercase())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Ingresar")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { onNavigateToRegister() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Ir a Registro")
            }
        }
    }
}
