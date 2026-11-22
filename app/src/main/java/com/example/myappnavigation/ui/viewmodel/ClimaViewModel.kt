package com.example.myappnavigation.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappnavigation.data.remote.ClimaActual
import com.example.myappnavigation.data.remote.Servicios
import com.example.myappnavigation.data.repository.ClimaRepository
import com.example.myappnavigation.data.repository.Resultado
import kotlinx.coroutines.launch

sealed interface EstadoClima {
    data object Cargando : EstadoClima
    data class Listo(val clima: ClimaActual) : EstadoClima
    data class Fallo(val mensaje: String) : EstadoClima
}

/** Muestra el clima actual de la base del vuelo (API externa Open-Meteo). */
class ClimaViewModel(private val repositorio: ClimaRepository) : ViewModel() {

    constructor() : this(Servicios.climaRepositorio)

    var estado by mutableStateOf<EstadoClima>(EstadoClima.Cargando)
        private set

    fun cargar(base: String) {
        viewModelScope.launch {
            estado = EstadoClima.Cargando
            estado = when (val resultado = repositorio.climaDe(base)) {
                is Resultado.Exito -> EstadoClima.Listo(resultado.datos)
                is Resultado.Error -> EstadoClima.Fallo(resultado.mensaje)
            }
        }
    }
}
