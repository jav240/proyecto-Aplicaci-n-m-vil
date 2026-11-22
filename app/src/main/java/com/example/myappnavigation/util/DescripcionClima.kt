package com.example.myappnavigation.util

import com.example.myappnavigation.data.remote.ClimaActual

/** Traduce el código WMO que entrega Open-Meteo a texto en español. */
object DescripcionClima {

    fun deCodigo(codigo: Int): String = when (codigo) {
        0 -> "Despejado"
        1, 2 -> "Mayormente despejado"
        3 -> "Nublado"
        45, 48 -> "Niebla"
        in 51..57 -> "Llovizna"
        in 61..67 -> "Lluvia"
        in 71..77 -> "Nieve"
        in 80..82 -> "Chubascos"
        in 95..99 -> "Tormenta"
        else -> "Sin información"
    }

    /** Referencia simple para el socio; la decisión final siempre es del piloto. */
    fun condicionParaVolar(clima: ClimaActual): String = when {
        clima.codigo in 95..99 || clima.codigo in 45..48 -> "No recomendable"
        clima.vientoKmh >= 35.0 || clima.codigo in 61..67 || clima.codigo in 80..82 -> "Condiciones dudosas"
        else -> "Condiciones favorables"
    }
}
