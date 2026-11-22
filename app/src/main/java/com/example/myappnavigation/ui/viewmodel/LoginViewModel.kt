package com.example.myappnavigation.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappnavigation.data.remote.Servicios
import com.example.myappnavigation.data.remote.UsuarioApi
import com.example.myappnavigation.data.repository.Resultado
import com.example.myappnavigation.data.repository.VuelosRepository
import com.example.myappnavigation.model.LoginModel
import com.example.myappnavigation.util.Validaciones
import kotlinx.coroutines.launch

class LoginViewModel(private val repositorio: VuelosRepository) : ViewModel() {

    constructor() : this(Servicios.vuelosRepositorio)

    // Estado que administra los datos del formulario
    var uiState by mutableStateOf(LoginModel())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var cargando by mutableStateOf(false)
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

    fun onLoginClick(onSuccess: (UsuarioApi) -> Unit) {
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
            cargando = true
            when (val resultado = repositorio.login(email, password)) {
                is Resultado.Exito -> {
                    errorMessage = null
                    onSuccess(resultado.datos)   // Inicia sesión con éxito
                }
                is Resultado.Error -> errorMessage = resultado.mensaje
            }
            cargando = false
        }
    }
}
