package com.example.myappnavigation.data.repository

import com.example.myappnavigation.data.remote.ApiConfig
import com.example.myappnavigation.data.remote.ClimaActual
import com.example.myappnavigation.data.remote.ClimaApi


object Bases {
    val coordenadas: Map<String, Pair<Double, Double>> = mapOf(
        "Tobalaba" to (-33.4636 to -70.5517),
        "Talcahuano" to (-36.7167 to -73.1167)
    )
}


class ClimaRepository(private val api: ClimaApi) {

    suspend fun climaDe(base: String): Resultado<ClimaActual> {
        val (latitud, longitud) = Bases.coordenadas[base]
            ?: return Resultado.Error("No hay coordenadas para la base " + base + ".")

        return Llamadas.ejecutar { api.actual(latitud, longitud, ApiConfig.CLIMA_VARIABLES) }
            .let { resultado ->
                when (resultado) {
                    is Resultado.Exito -> resultado.datos.current
                        ?.let { Resultado.Exito(it) }
                        ?: Resultado.Error("El servicio de clima no devolvió datos.")
                    is Resultado.Error -> resultado
                }
            }
    }
}
