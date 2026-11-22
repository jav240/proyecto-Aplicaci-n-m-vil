package com.example.myappnavigation.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappnavigation.data.remote.ReservaAdminApi
import com.example.myappnavigation.data.remote.Servicios
import com.example.myappnavigation.data.remote.SocioApi
import com.example.myappnavigation.data.repository.Resultado
import com.example.myappnavigation.data.repository.VuelosRepository
import com.example.myappnavigation.model.VueloModel
import kotlinx.coroutines.launch

class AdminViewModel(private val repositorio: VuelosRepository) : ViewModel() {

    constructor() : this(Servicios.vuelosRepositorio)

    /** Socios registrados (el backend no incluye a los administradores). */
    var socios by mutableStateOf<List<SocioApi>>(emptyList())
        private set

    var reservas by mutableStateOf<List<ReservaAdminApi>>(emptyList())
        private set

    var vuelos by mutableStateOf<List<VueloModel>>(emptyList())
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    /** GET /admin/socios, GET /admin/reservas y GET /vuelos. */
    fun cargar() {
        viewModelScope.launch {
            cargando = true
            error = null

            when (val r = repositorio.socios()) {
                is Resultado.Exito -> socios = r.datos
                is Resultado.Error -> error = r.mensaje
            }
            when (val r = repositorio.reservasAdmin()) {
                is Resultado.Exito -> reservas = r.datos
                is Resultado.Error -> error = r.mensaje
            }
            when (val r = repositorio.vuelos(null)) {
                is Resultado.Exito -> vuelos = r.datos
                is Resultado.Error -> error = r.mensaje
            }

            cargando = false
        }
    }
}
