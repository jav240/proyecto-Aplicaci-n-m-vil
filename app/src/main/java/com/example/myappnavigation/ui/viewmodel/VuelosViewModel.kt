package com.example.myappnavigation.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappnavigation.data.remote.Servicios
import com.example.myappnavigation.data.repository.Resultado
import com.example.myappnavigation.data.repository.VuelosRepository
import com.example.myappnavigation.model.VueloModel
import kotlinx.coroutines.launch

class VuelosViewModel(private val repositorio: VuelosRepository) : ViewModel() {

    constructor() : this(Servicios.vuelosRepositorio)

    var fechaFiltro by mutableStateOf<String?>(null)
        private set

    var vuelosFiltrados by mutableStateOf<List<VueloModel>>(emptyList())
        private set

    /** Verdadero cuando no hubo conexión y la lista es la última copia guardada en Room. */
    var mostrandoGuardados by mutableStateOf(false)
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    /** GET /vuelos (con ?fecha= si hay filtro). */
    fun cargar() {
        viewModelScope.launch {
            cargando = true
            error = null
            when (val resultado = repositorio.vuelos(fechaFiltro)) {
                is Resultado.Exito -> {
                    vuelosFiltrados = resultado.datos
                    mostrandoGuardados = resultado.desdeCache
                }
                is Resultado.Error -> {
                    vuelosFiltrados = emptyList()
                    mostrandoGuardados = false
                    error = resultado.mensaje
                }
            }
            cargando = false
        }
    }

    fun onFechaSeleccionada(year: Int, month: Int, day: Int) {
        fechaFiltro = "%04d-%02d-%02d".format(year, month + 1, day)
        cargar()
    }

    fun quitarFiltro() {
        fechaFiltro = null
        cargar()
    }
}
