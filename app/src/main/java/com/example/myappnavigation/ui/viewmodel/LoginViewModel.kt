package com.example.myappnavigation.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappnavigation.data.database.AppDatabase
import com.example.myappnavigation.model.LoginModel
import com.example.myappnavigation.util.Seguridad
import com.example.myappnavigation.util.Validaciones
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // Estado que administra los datos del formulario
    var uiState by mutableStateOf(LoginModel())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onEmailChanged(email: String) {
        uiState = uiState.copy(
            email = email,
            emailError = if (email.isEmpty()) null else Validaciones.email(email)
        )
        errorMessage = null
    }

    fun onPasswordChanged(password: String) {
        uiState = uiState.copy(
            password = password,
            passwordError = if (password.isEmpty()) null else Validaciones.password(password)
        )
        errorMessage = null
    }

    fun onLoginClick(context: Context, onSuccess: () -> Unit) {
        val email = uiState.email.trim().lowercase()
        val password = uiState.password

        val emailError = Validaciones.email(email)
        val passwordError = Validaciones.password(password)
        uiState = uiState.copy(emailError = emailError, passwordError = passwordError)
        if (emailError != null || passwordError != null) {
            errorMessage = "Revisa los campos marcados."
            return
        }

        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val user = db.usuarioDao().getUserByEmail(email)

            if (user != null && user.password == Seguridad.hash(password)) {
                errorMessage = null
                onSuccess()   // Inicia sesión con éxito
            } else {
                errorMessage = "Correo o contraseña incorrectos."
            }
        }
    }
}
