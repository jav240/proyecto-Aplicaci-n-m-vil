package com.example.myappnavigation.data.repository

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.example.myappnavigation.data.remote.ErrorApi
import java.io.IOException
import retrofit2.Response


sealed class Resultado<out T> {

    data class Exito<T>(val datos: T, val desdeCache: Boolean = false) : Resultado<T>()

    data class Error(
        val mensaje: String,
        val campos: Map<String, String> = emptyMap(),
        val codigo: Int? = null
    ) : Resultado<Nothing>() {

        val sinRespuesta: Boolean get() = codigo == null || codigo >= 500
    }
}

inline fun <T, R> Resultado<T>.mapear(transformar: (T) -> R): Resultado<R> =
    when (this) {
        is Resultado.Exito -> Resultado.Exito(transformar(datos), desdeCache)
        is Resultado.Error -> this
    }


internal object Llamadas {
    private val gson = Gson()

    suspend fun <T> ejecutar(llamada: suspend () -> Response<T>): Resultado<T> =
        try {
            val respuesta = llamada()
            if (respuesta.isSuccessful) {
                val cuerpo = respuesta.body()
                if (cuerpo != null) {
                    Resultado.Exito(cuerpo)
                } else {
                    Resultado.Error("El servidor respondió sin datos.", codigo = respuesta.code())
                }
            } else {
                errorDe(respuesta)
            }
        } catch (e: IOException) {
            Resultado.Error("No se pudo conectar con el servidor. Revisa tu conexión.")
        } catch (e: JsonParseException) {
            Resultado.Error("La respuesta del servidor no es válida.")
        }

    private fun <T> errorDe(respuesta: Response<T>): Resultado.Error {
        val crudo = try {
            respuesta.errorBody()?.string()
        } catch (e: IOException) {
            null
        }
        val detalle = try {
            if (crudo.isNullOrBlank()) null else gson.fromJson(crudo, ErrorApi::class.java)
        } catch (e: JsonParseException) {
            null
        }
        return Resultado.Error(
            mensaje = detalle?.error ?: "Error del servidor (" + respuesta.code() + ").",
            campos = detalle?.campos.orEmpty(),
            codigo = respuesta.code()
        )
    }
}
