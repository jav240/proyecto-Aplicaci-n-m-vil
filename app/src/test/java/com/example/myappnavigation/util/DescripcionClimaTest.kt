package com.example.myappnavigation.util

import com.example.myappnavigation.data.remote.ClimaActual
import org.junit.Assert.assertEquals
import org.junit.Test

class DescripcionClimaTest {

    @Test fun traduce_codigos_wmo() {
        assertEquals("Despejado", DescripcionClima.deCodigo(0))
        assertEquals("Mayormente despejado", DescripcionClima.deCodigo(2))
        assertEquals("Nublado", DescripcionClima.deCodigo(3))
        assertEquals("Niebla", DescripcionClima.deCodigo(45))
        assertEquals("Llovizna", DescripcionClima.deCodigo(53))
        assertEquals("Lluvia", DescripcionClima.deCodigo(63))
        assertEquals("Nieve", DescripcionClima.deCodigo(73))
        assertEquals("Chubascos", DescripcionClima.deCodigo(81))
        assertEquals("Tormenta", DescripcionClima.deCodigo(95))
        assertEquals("Sin información", DescripcionClima.deCodigo(999))
    }

    @Test fun condicion_para_volar_segun_viento_y_clima() {
        assertEquals("Condiciones favorables", DescripcionClima.condicionParaVolar(ClimaActual(20.0, 10.0, 0)))
        assertEquals("Condiciones dudosas", DescripcionClima.condicionParaVolar(ClimaActual(20.0, 40.0, 0)))
        assertEquals("Condiciones dudosas", DescripcionClima.condicionParaVolar(ClimaActual(12.0, 5.0, 63)))
        assertEquals("No recomendable", DescripcionClima.condicionParaVolar(ClimaActual(15.0, 5.0, 96)))
        assertEquals("No recomendable", DescripcionClima.condicionParaVolar(ClimaActual(8.0, 5.0, 45)))
    }
}
