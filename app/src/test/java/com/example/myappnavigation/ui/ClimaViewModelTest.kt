package com.example.myappnavigation.ui

import com.example.myappnavigation.MainDispatcherRule
import com.example.myappnavigation.data.remote.ClimaActual
import com.example.myappnavigation.data.repository.ClimaRepository
import com.example.myappnavigation.data.repository.Resultado
import com.example.myappnavigation.ui.viewmodel.ClimaViewModel
import com.example.myappnavigation.ui.viewmodel.EstadoClima
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ClimaViewModelTest {
    @get:Rule val main = MainDispatcherRule()

    private val repo = mockk<ClimaRepository>()
    private val vm = ClimaViewModel(repo)

    @Test fun estado_inicial_es_cargando() {
        assertTrue(vm.estado is EstadoClima.Cargando)
    }

    @Test fun clima_cargado_con_exito() {
        val clima = ClimaActual(21.0, 8.0, 1)
        coEvery { repo.climaDe("Tobalaba") } returns Resultado.Exito(clima)

        vm.cargar("Tobalaba")

        assertEquals(EstadoClima.Listo(clima), vm.estado)
    }

    @Test fun clima_con_error() {
        coEvery { repo.climaDe("Tobalaba") } returns Resultado.Error("No se pudo conectar con el servidor.")

        vm.cargar("Tobalaba")

        assertEquals(EstadoClima.Fallo("No se pudo conectar con el servidor."), vm.estado)
    }
}
