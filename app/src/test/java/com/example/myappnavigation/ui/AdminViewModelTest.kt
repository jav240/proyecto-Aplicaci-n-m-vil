package com.example.myappnavigation.ui

import com.example.myappnavigation.Datos
import com.example.myappnavigation.MainDispatcherRule
import com.example.myappnavigation.data.remote.ReservaAdminApi
import com.example.myappnavigation.data.remote.SocioApi
import com.example.myappnavigation.data.repository.Resultado
import com.example.myappnavigation.data.repository.VuelosRepository
import com.example.myappnavigation.ui.viewmodel.AdminViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class AdminViewModelTest {
    @get:Rule val main = MainDispatcherRule()

    private val repo = mockk<VuelosRepository>()
    private val vm = AdminViewModel(repo)

    private val socio = SocioApi(1, "Ana Pérez", "ana@correo.cl", null)
    private val reserva = ReservaAdminApi("Ana Pérez", "ana@correo.cl", Datos.vuelo1, true, null)

    @Test fun cargar_trae_socios_reservas_y_vuelos() {
        coEvery { repo.socios() } returns Resultado.Exito(listOf(socio))
        coEvery { repo.reservasAdmin() } returns Resultado.Exito(listOf(reserva))
        coEvery { repo.vuelos(null) } returns Resultado.Exito(listOf(Datos.vuelo1))

        vm.cargar()

        assertEquals(listOf(socio), vm.socios)
        assertEquals(listOf(reserva), vm.reservas)
        assertEquals(listOf(Datos.vuelo1), vm.vuelos)
        assertNull(vm.error)
        assertFalse(vm.cargando)
    }

    @Test fun cualquier_error_queda_visible() {
        coEvery { repo.socios() } returns Resultado.Error("Solo para administradores.", codigo = 403)
        coEvery { repo.reservasAdmin() } returns Resultado.Error("Solo para administradores.", codigo = 403)
        coEvery { repo.vuelos(null) } returns Resultado.Exito(listOf(Datos.vuelo1))

        vm.cargar()

        assertEquals("Solo para administradores.", vm.error)
        assertEquals(emptyList<Any>(), vm.socios)
        assertEquals(listOf(Datos.vuelo1), vm.vuelos)
        assertFalse(vm.cargando)
    }

    @Test fun error_al_cargar_vuelos() {
        coEvery { repo.socios() } returns Resultado.Exito(emptyList())
        coEvery { repo.reservasAdmin() } returns Resultado.Exito(emptyList())
        coEvery { repo.vuelos(null) } returns Resultado.Error("No se pudo conectar con el servidor.")

        vm.cargar()

        assertEquals("No se pudo conectar con el servidor.", vm.error)
    }
}
