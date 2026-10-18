package com.example.myappnavigation.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myappnavigation.model.VueloModel

/** Estado compartido entre pantallas mientras el socio tiene la sesión iniciada. */
class SesionViewModel : ViewModel() {

    var correo by mutableStateOf("")
        private set

    var vueloSeleccionado by mutableStateOf<VueloModel?>(null)
        private set

    var vueloAceptado by mutableStateOf<VueloModel?>(null)
        private set

    var consentimientoFirmado by mutableStateOf(false)
        private set

    val tieneVuelo: Boolean
        get() = vueloAceptado != null

    fun iniciarSesion(nuevoCorreo: String) {
        correo = nuevoCorreo
    }

    fun cerrarSesion() {
        correo = ""
        vueloSeleccionado = null
        vueloAceptado = null
        consentimientoFirmado = false
    }

    fun seleccionarVuelo(vuelo: VueloModel) {
        vueloSeleccionado = vuelo
    }

    /** Acepta el vuelo elegido. Devuelve false si no había ninguno seleccionado. */
    fun aceptarVueloSeleccionado(): Boolean {
        val vuelo = vueloSeleccionado ?: return false
        if (vueloAceptado?.id != vuelo.id) {
            vueloAceptado = vuelo
            consentimientoFirmado = false
        }
        return true
    }

    fun onConsentimientoChanged(firmado: Boolean) {
        if (tieneVuelo) {
            consentimientoFirmado = firmado
        }
    }
}
