package com.example.myappnavigation.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappnavigation.data.remote.ReservaApi
import com.example.myappnavigation.data.remote.Servicios
import com.example.myappnavigation.data.remote.UsuarioApi
import com.example.myappnavigation.data.repository.Resultado
import com.example.myappnavigation.data.repository.VuelosRepository
import com.example.myappnavigation.model.VueloModel
import kotlinx.coroutines.launch

/**
 * Estado compartido entre pantallas mientras el usuario tiene la sesión iniciada.
 * El rol lo decide el backend; la reserva y el consentimiento viven en el microservicio.
 */
class SesionViewModel(private val repositorio: VuelosRepository) : ViewModel() {

    constructor() : this(Servicios.vuelosRepositorio)

    var correo by mutableStateOf("")
        private set

    var nombre by mutableStateOf("")
        private set

    var rol by mutableStateOf(UsuarioApi.ROL_SOCIO)
        private set

    val esAdmin: Boolean
        get() = rol == UsuarioApi.ROL_ADMIN

    var vueloSeleccionado by mutableStateOf<VueloModel?>(null)
        private set

    var vueloAceptado by mutableStateOf<VueloModel?>(null)
        private set

    var consentimientoFirmado by mutableStateOf(false)
        private set

    /** Último mensaje de error o aviso para mostrar en pantalla. */
    var mensaje by mutableStateOf<String?>(null)
        private set

    val tieneVuelo: Boolean
        get() = vueloAceptado != null

    val haySesion: Boolean
        get() = correo.isNotEmpty()

    /** Verdadero mientras se lee de Room si quedó una sesión guardada (al abrir la app). */
    var restaurando by mutableStateOf(true)
        private set

    /** El servidor rechazó el token guardado (venció): la pantalla debe llevar al login. */
    var sesionExpirada by mutableStateOf(false)
        private set

    /** Recupera la sesión guardada en Room para no pedir el login de nuevo. Se llama una sola vez. */
    fun restaurarSesion() {
        if (!restaurando) return
        viewModelScope.launch {
            val usuario = repositorio.restaurarSesion()
            if (usuario != null) iniciarSesion(usuario)
            restaurando = false
        }
    }

    fun sesionExpiradaAtendida() {
        sesionExpirada = false
    }

    fun iniciarSesion(usuario: UsuarioApi) {
        correo = usuario.email
        nombre = usuario.nombre
        rol = usuario.rol
        mensaje = null
        if (!esAdmin) cargarReserva()
    }

    /** GET /reserva: recupera la reserva guardada en el servidor. */
    fun cargarReserva() {
        viewModelScope.launch {
            when (val resultado = repositorio.reserva()) {
                is Resultado.Exito -> aplicarReserva(resultado.datos)
                is Resultado.Error -> manejarError(resultado)
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch { repositorio.cerrarSesion() }   // borra token y sesión de Room
        correo = ""
        nombre = ""
        rol = UsuarioApi.ROL_SOCIO
        vueloSeleccionado = null
        vueloAceptado = null
        consentimientoFirmado = false
        mensaje = null
    }

    fun seleccionarVuelo(vuelo: VueloModel) {
        vueloSeleccionado = vuelo
    }

    /** PUT /reserva: acepta el vuelo elegido (o cambia la reserva anterior). */
    fun aceptarVueloSeleccionado(onTerminado: (Boolean) -> Unit) {
        val vuelo = vueloSeleccionado
        if (vuelo == null) {
            onTerminado(false)
            return
        }
        viewModelScope.launch {
            when (val resultado = repositorio.aceptarVuelo(vuelo.id)) {
                is Resultado.Exito -> {
                    mensaje = null
                    aplicarReserva(resultado.datos)
                    onTerminado(true)
                }
                is Resultado.Error -> {
                    manejarError(resultado)
                    onTerminado(false)
                }
            }
        }
    }

    /** PUT /reserva/consentimiento. */
    fun onConsentimientoChanged(firmado: Boolean) {
        if (!tieneVuelo) return
        viewModelScope.launch {
            when (val resultado = repositorio.firmarConsentimiento(firmado)) {
                is Resultado.Exito -> {
                    mensaje = null
                    aplicarReserva(resultado.datos)
                }
                is Resultado.Error -> manejarError(resultado)
            }
        }
    }

    /** DELETE /reserva: cancela la reserva y devuelve el cupo. */
    fun cancelarReserva() {
        if (!tieneVuelo) return
        viewModelScope.launch {
            when (val resultado = repositorio.cancelarReserva()) {
                is Resultado.Exito -> {
                    vueloAceptado = null
                    consentimientoFirmado = false
                    mensaje = "Tu reserva fue cancelada."
                }
                is Resultado.Error -> manejarError(resultado)
            }
        }
    }

    /** Un 401 con sesión iniciada significa token vencido o inválido: se cierra la sesión local. */
    private fun manejarError(error: Resultado.Error) {
        if (error.codigo == 401) {
            cerrarSesion()
            sesionExpirada = true
            mensaje = "Tu sesión expiró. Inicia sesión nuevamente."
        } else {
            mensaje = error.mensaje
        }
    }

    private fun aplicarReserva(reserva: ReservaApi?) {
        vueloAceptado = reserva?.vuelo
        consentimientoFirmado = reserva?.consentimientoFirmado ?: false
    }
}
