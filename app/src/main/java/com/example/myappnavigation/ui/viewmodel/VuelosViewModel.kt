package com.example.myappnavigation.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myappnavigation.data.VuelosFicticios
import com.example.myappnavigation.model.VueloModel

class VuelosViewModel : ViewModel() {

    private val todos = VuelosFicticios.lista

    /** Fecha elegida en el calendario (yyyy-MM-dd) o null si no hay filtro. */
    var fechaFiltro by mutableStateOf<String?>(null)
        private set

    val vuelosFiltrados: List<VueloModel>
        get() {
            val fecha = fechaFiltro
            return if (fecha == null) todos else todos.filter { it.fechaIso == fecha }
        }

    // El calendario nativo entrega el mes de 0 a 11
    fun onFechaSeleccionada(year: Int, month: Int, day: Int) {
        fechaFiltro = "%04d-%02d-%02d".format(year, month + 1, day)
    }

    fun quitarFiltro() {
        fechaFiltro = null
    }
}
