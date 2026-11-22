package com.example.myappnavigation.ui

import com.example.myappnavigation.Datos
import com.example.myappnavigation.MainDispatcherRule
import com.example.myappnavigation.data.repository.Resultado
import com.example.myappnavigation.data.repository.VuelosRepository
import com.example.myappnavigation.ui.viewmodel.VuelosViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class VuelosViewModelTest {
    @get:Rule val main = MainDispatcherRule()

    private val repo = mockk<VuelosRepository>()
    private val vm = VuelosViewModel(repo)

    @Test fun cargar_trae_los_vuelos() {
        coEvery { repo.vuelos(null) } returns Resultado.Exito(listOf(Datos.vuelo1, Datos.vuelo2))

        vm.cargar()

        assertEquals(listOf(Datos.vuelo1, Datos.vuelo2), vm.vuelosFiltrados)
        assertNull(vm.error)
        assertFalse(vm.cargando)
    }

    @Test fun cargar_con_error_deja_lista_vacia_y_mensaje() {
        coEvery { repo.vuelos(null) } returns Resultado.Error("No se pudo conectar con el servidor.")

        vm.cargar()

        assertEquals(emptyList<Any>(), vm.vuelosFiltrados)
        assertEquals("No se pudo conectar con el servidor.", vm.error)
        assertFalse(vm.cargando)
    }

    @Test fun seleccionar_fecha_filtra_en_el_servidor() {
        coEvery { repo.vuelos("2026-11-07") } returns Resultado.Exito(listOf(Datos.vuelo1))

        vm.onFechaSeleccionada(2026, 10, 7) // el mes del DatePicker parte en 0

        assertEquals("2026-11-07", vm.fechaFiltro)
        assertEquals(listOf(Datos.vuelo1), vm.vuelosFiltrados)
    }

    @Test fun quitar_filtro_vuelve_a_cargar_todos() {
        coEvery { repo.vuelos("2026-11-07") } returns Resultado.Exito(listOf(Datos.vuelo1))
        coEvery { repo.vuelos(null) } returns Resultado.Exito(listOf(Datos.vuelo1, Datos.vuelo2))
        vm.onFechaSeleccionada(2026, 10, 7)

        vm.quitarFiltro()

        assertNull(vm.fechaFiltro)
        assertEquals(2, vm.vuelosFiltrados.size)
        coVerify { repo.vuelos(null) }
    }

    @Test fun sin_conexion_muestra_los_vuelos_guardados_y_lo_avisa() {
        coEvery { repo.vuelos(null) } returns Resultado.Exito(listOf(Datos.vuelo1), desdeCache = true)

        vm.cargar()

        assertEquals(listOf(Datos.vuelo1), vm.vuelosFiltrados)
        assertTrue(vm.mostrandoGuardados)
        assertNull(vm.error)
    }

    @Test fun al_volver_la_conexion_se_quita_el_aviso() {
        coEvery { repo.vuelos(null) } returnsMany listOf(
            Resultado.Exito(listOf(Datos.vuelo1), desdeCache = true),
            Resultado.Exito(listOf(Datos.vuelo1, Datos.vuelo2))
        )

        vm.cargar()
        vm.cargar()

        assertFalse(vm.mostrandoGuardados)
        assertEquals(2, vm.vuelosFiltrados.size)
    }
}
