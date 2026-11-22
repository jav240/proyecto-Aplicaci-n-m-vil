package com.example.myappnavigation.util

import com.example.myappnavigation.BuildConfig

/**
 * Los datos del remitente SMTP salen de local.properties (que NO se sube a GitHub):
 *   smtp.remitente=tu_cuenta@gmail.com
 *   smtp.clave=contraseña_de_aplicación_de_google
 * Si quedan vacíos, la app abre la app de correo del dispositivo como respaldo.
 */
object ConfigCorreo {
    val REMITENTE: String = BuildConfig.SMTP_REMITENTE
    val CLAVE_APP: String = BuildConfig.SMTP_CLAVE
    const val HOST = "smtp.gmail.com"
    const val PUERTO = "587"
}
