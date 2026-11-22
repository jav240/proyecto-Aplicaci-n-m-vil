package com.example.myappnavigation.data.remote

import com.example.myappnavigation.model.VueloModel
import com.google.gson.annotations.SerializedName



data class RegistroRequest(val nombre: String, val email: String, val password: String)

data class LoginRequest(val email: String, val password: String)

data class UsuarioApi(
    val id: Long,
    val nombre: String,
    val email: String,
    val rol: String
) {
    val esAdmin: Boolean get() = rol == ROL_ADMIN

    companion object {
        const val ROL_ADMIN = "administrador"
        const val ROL_SOCIO = "socio"
    }
}

data class SesionApi(val token: String, val usuario: UsuarioApi)

data class AceptarRequest(val vueloId: Int)

data class ConsentimientoRequest(val firmado: Boolean)

data class ReservaApi(
    val vuelo: VueloModel,
    val consentimientoFirmado: Boolean,
    val creadoEn: String?
)


data class ReservaRespuesta(val reserva: ReservaApi?)

data class SocioApi(val id: Long, val nombre: String, val email: String, val creadoEn: String?)

data class ReservaAdminApi(
    val socio: String,
    val socioEmail: String,
    val vuelo: VueloModel,
    val consentimientoFirmado: Boolean,
    val creadoEn: String?
)


data class ErrorApi(val error: String?, val campos: Map<String, String>?)

// ---------- API externa (Open-Meteo, sin clave) ----------

data class ClimaRespuesta(val current: ClimaActual?)

data class ClimaActual(
    @SerializedName("temperature_2m") val temperatura: Double,
    @SerializedName("wind_speed_10m") val vientoKmh: Double,
    @SerializedName("weather_code") val codigo: Int,
    val time: String? = null
)
