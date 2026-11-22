package com.example.myappnavigation.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappnavigation.data.remote.Servicios
import com.example.myappnavigation.data.repository.Resultado
import com.example.myappnavigation.data.repository.VuelosRepository
import com.example.myappnavigation.model.RegisterModel
import com.example.myappnavigation.util.Validaciones
import kotlinx.coroutines.launch

class RegisterViewModel(private val repositorio: VuelosRepository) : ViewModel() {

    constructor() : this(Servicios.vuelosRepositorio)

    var uiState by mutableStateOf(RegisterModel())
        private set

    var cargando by mutableStateOf(false)
        private set

    /** Verdadero cuando todos los campos cumplen las reglas (sirve para animar el botón). */
    val formularioValido: Boolean
        get() = uiState.let {
            Validaciones.nombre(it.name) == null &&
                Validaciones.email(it.email) == null &&
                Validaciones.password(it.password) == null &&
                Validaciones.confirmar(it.password, it.confirmPassword) == null
        }

    fun onNameChanged(name: String) {
        uiState = uiState.copy(
            name = name,
            nameError = if (name.isEmpty()) null else Validaciones.nombre(name),
            errorMessage = null
        )
    }

    fun onEmailChanged(email: String) {
        uiState = uiState.copy(
            email = email,
            emailError = if (email.isEmpty()) null else Validaciones.email(email),
            errorMessage = null
        )
    }

    fun onPasswordChanged(password: String) {
        val s = uiState
        uiState = s.copy(
            password = password,
            passwordError = if (password.isEmpty()) null else Validaciones.password(password),
            confirmError = if (s.confirmPassword.isEmpty()) null else Validaciones.confirmar(password, s.confirmPassword),
            errorMessage = null
        )
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        uiState = uiState.copy(
            confirmPassword = confirmPassword,
            confirmError = if (confirmPassword.isEmpty()) null else Validaciones.confirmar(uiState.password, confirmPassword),
            errorMessage = null
        )
    }

    fun onRegisterClick(onSuccess: () -> Unit) {
        val s = uiState

        val nameError = Validaciones.nombre(s.name)
        val emailError = Validaciones.email(s.email)
        val passwordError = Validaciones.password(s.password)
        val confirmError = Validaciones.confirmar(s.password, s.confirmPassword)

        if (nameError != null || emailError != null || passwordError != null || confirmError != null) {
            uiState = s.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmError = confirmError,
                errorMessage = "Revisa los campos marcados."
            )
            return
        }

        // Crea la cuenta en el microservicio (POST /auth/register)
        viewModelScope.launch {
            cargando = true
            when (val resultado = repositorio.register(s.name, s.email, s.password)) {
                is Resultado.Exito -> {
                    uiState = RegisterModel()
                    onSuccess()
                }
                is Resultado.Error -> {
                    uiState = uiState.copy(
                        nameError = resultado.campos["nombre"],
                        emailError = resultado.campos["email"],
                        passwordError = resultado.campos["password"],
                        errorMessage = resultado.mensaje
                    )
                }
            }
            cargando = false
        }
    }
}
