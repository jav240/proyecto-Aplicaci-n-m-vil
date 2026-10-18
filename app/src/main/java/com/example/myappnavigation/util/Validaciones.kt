package com.example.myappnavigation.util

import java.security.MessageDigest

/** Reglas de validación centralizadas: la interfaz solo muestra el mensaje que se devuelve. */
object Validaciones {
    private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu nombre completo."
        valor.trim().length < 3 -> "El nombre es muy corto."
        else -> null
    }

    fun email(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu correo."
        !EMAIL.matches(valor.trim()) -> "El correo no es válido."
        else -> null
    }

    fun password(valor: String): String? = when {
        valor.isBlank() -> "Ingresa una contraseña."
        valor.length < 6 -> "Debe tener al menos 6 caracteres."
        else -> null
    }

    fun confirmar(password: String, confirmacion: String): String? = when {
        confirmacion.isBlank() -> "Repite la contraseña."
        password != confirmacion -> "Las contraseñas no coinciden."
        else -> null
    }
}

object Seguridad {
    /** La contraseña nunca se guarda en texto plano en la base de datos. */
    fun hash(texto: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
