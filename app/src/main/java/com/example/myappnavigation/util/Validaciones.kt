package com.example.myappnavigation.util

import java.security.MessageDigest


object Validaciones {
    private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu nombre completo."
        valor.trim().length < 3 -> "El nombre es muy corto."
        !valor.trim().contains(" ") -> "Ingresa nombre y apellido."
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
        !valor.any { it.isDigit() } -> "Debe incluir al menos un número."
        else -> null
    }

    fun confirmar(password: String, confirmacion: String): String? = when {
        confirmacion.isBlank() -> "Repite la contraseña."
        password != confirmacion -> "Las contraseñas no coinciden."
        else -> null
    }
}

object Seguridad {

    fun hash(texto: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
