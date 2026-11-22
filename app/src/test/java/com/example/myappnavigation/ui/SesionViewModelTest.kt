package com.example.myappnavigation.ui

import com.example.myappnavigation.Datos
import com.example.myappnavigation.MainDispatcherRule
import com.example.myappnavigation.data.remote.UsuarioApi
import com.example.myappnavigation.data.repository.Resultado
import com.example.myappnavigation.data.repository.VuelosRepository
import com.example.myappnavigation.ui.viewmodel.SesionViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SesionViewModelTest {
    @get:Rule val main = MainDispatcherRule()

    private val repo = mockk<VuelosRepository>()
    private val vm = SesionViewModel(repo)

    @Test fun iniciar_sesion_de_socio_guarda_datos_y_recupera_la_reserva() {
        coEvery { repo.reserva() } returns Resultado.Exito(Datos.reserva(true))

        vm.iniciarSesion(Datos.socio)

        assertEquals("ana@correo.cl", vm.correo)
        assertEquals("Ana Pérez", vm.nombre)
        assertFalse(vm.esAdmin)
        assertEquals(Datos.vuelo1, vm.vueloAceptado)
        assertTrue(vm.consentimientoFirmado)
        assertTrue(vm.tieneVuelo)
    }

    @Test fun iniciar_sesion_de_admin_no_pide_reserva() {
        vm.iniciarSesion(Datos.admin)

        assertTrue(vm.esAdmin)
        assertEquals(UsuarioApi.ROL_ADMIN, vm.rol)
        coVerify(exactly = 0) { repo.reserva() }
    }

    @Test fun socio_sin_reserva_queda_sin_vuelo() {
        coEvery { repo.reserva() } returns Resultado.Exito(null)

        vm.iniciarSesion(Datos.socio)

        assertNull(vm.vueloAceptado)
        assertFalse(vm.tieneVuelo)
        assertFalse(vm.consentimientoFirmado)
    }

    @Test fun error_al_cargar_la_reserva_deja_un_mensaje() {
        coEvery { repo.reserva() } returns Resultado.Error("No se pudo conectar con el servidor.")

        vm.iniciarSesion(Datos.socio)

        assertEquals("No se pudo conectar con el servidor.", vm.mensaje)
    }

    @Test fun aceptar_sin_vuelo_seleccionado_falla() {
        var resultado: Boolean? = null

        vm.aceptarVueloSeleccionado { resultado = it }

        assertEquals(false, resultado)
        coVerify(exactly = 0) { repo.aceptarVuelo(any()) }
    }

    @Test fun aceptar_vuelo_guarda_la_reserva_del_servidor() {
        coEvery { repo.aceptarVuelo(1) } returns Resultado.Exito(Datos.reserva())
        vm.seleccionarVuelo(Datos.vuelo1)
        var resultado: Boolean? = null

        vm.aceptarVueloSeleccionado { resultado = it }

        assertEquals(true, resultado)
        assertEquals(Datos.vuelo1, vm.vueloAceptado)
        assertFalse(vm.consentimientoFirmado)
        assertNull(vm.mensaje)
    }

    @Test fun aceptar_vuelo_sin_cupos_muestra_el_error() {
        coEvery { repo.aceptarVuelo(1) } returns Resultado.Error("Ese vuelo ya no tiene cupos.", codigo = 409)
        vm.seleccionarVuelo(Datos.vuelo1)
        var resultado: Boolean? = null

        vm.aceptarVueloSeleccionado { resultado = it }

        assertEquals(false, resultado)
        assertEquals("Ese vuelo ya no tiene cupos.", vm.mensaje)
        assertNull(vm.vueloAceptado)
    }

    @Test fun firmar_consentimiento_actualiza_el_estado() {
        coEvery { repo.aceptarVuelo(1) } returns Resultado.Exito(Datos.reserva())
        coEvery { repo.firmarConsentimiento(true) } returns Resultado.Exito(Datos.reserva(true))
        vm.seleccionarVuelo(Datos.vuelo1)
        vm.aceptarVueloSeleccionado { }

        vm.onConsentimientoChanged(true)

        assertTrue(vm.consentimientoFirmado)
    }

    @Test fun firmar_sin_vuelo_no_hace_nada() {
        vm.onConsentimientoChanged(true)

        coVerify(exactly = 0) { repo.firmarConsentimiento(any()) }
    }

    @Test fun error_al_firmar_deja_mensaje() {
        coEvery { repo.aceptarVuelo(1) } returns Resultado.Exito(Datos.reserva())
        coEvery { repo.firmarConsentimiento(true) } returns Resultado.Error("No se pudo conectar con el servidor.")
        vm.seleccionarVuelo(Datos.vuelo1)
        vm.aceptarVueloSeleccionado { }

        vm.onConsentimientoChanged(true)

        assertFalse(vm.consentimientoFirmado)
        assertEquals("No se pudo conectar con el servidor.", vm.mensaje)
        assertFalse(vm.sesionExpirada)
    }

    @Test fun token_rechazado_cierra_la_sesion_y_marca_que_expiro() {
        coEvery { repo.cerrarSesion() } just Runs
        coEvery { repo.reserva() } returns Resultado.Error("Sesión inválida.", codigo = 401)

        vm.iniciarSesion(Datos.socio)

        assertTrue(vm.sesionExpirada)
        assertFalse(vm.haySesion)
        assertEquals("Tu sesión expiró. Inicia sesión nuevamente.", vm.mensaje)
        coVerify { repo.cerrarSesion() }
        vm.sesionExpiradaAtendida()
        assertFalse(vm.sesionExpirada)
    }

    // ---------- Sesión guardada ----------

    @Test fun restaurar_sesion_de_socio_recupera_datos_y_reserva() {
        coEvery { repo.restaurarSesion() } returns Datos.socio
        coEvery { repo.reserva() } returns Resultado.Exito(Datos.reserva())

        assertTrue(vm.restaurando)
        vm.restaurarSesion()

        assertFalse(vm.restaurando)
        assertTrue(vm.haySesion)
        assertEquals("ana@correo.cl", vm.correo)
        assertFalse(vm.esAdmin)
        assertEquals(Datos.vuelo1, vm.vueloAceptado)
    }

    @Test fun restaurar_sesion_de_admin_conserva_el_rol() {
        coEvery { repo.restaurarSesion() } returns Datos.admin

        vm.restaurarSesion()

        assertTrue(vm.esAdmin)
        coVerify(exactly = 0) { repo.reserva() }
    }

    @Test fun restaurar_sin_sesion_guardada_deja_todo_vacio() {
        coEvery { repo.restaurarSesion() } returns null

        vm.restaurarSesion()

        assertFalse(vm.restaurando)
        assertFalse(vm.haySesion)
    }

    @Test fun restaurar_sesion_se_ejecuta_una_sola_vez() {
        coEvery { repo.restaurarSesion() } returns null

        vm.restaurarSesion()
        vm.restaurarSesion()

        coVerify(exactly = 1) { repo.restaurarSesion() }
    }

    @Test fun cancelar_reserva_limpia_vuelo_y_consentimiento() {
        coEvery { repo.aceptarVuelo(1) } returns Resultado.Exito(Datos.reserva(true))
        coEvery { repo.cancelarReserva() } returns Resultado.Exito(Unit)
        vm.seleccionarVuelo(Datos.vuelo1)
        vm.aceptarVueloSeleccionado { }

        vm.cancelarReserva()

        assertNull(vm.vueloAceptado)
        assertFalse(vm.consentimientoFirmado)
        assertEquals("Tu reserva fue cancelada.", vm.mensaje)
    }

    @Test fun cancelar_sin_reserva_no_llama_al_servidor() {
        vm.cancelarReserva()

        coVerify(exactly = 0) { repo.cancelarReserva() }
    }

    @Test fun error_al_cancelar_mantiene_la_reserva() {
        coEvery { repo.aceptarVuelo(1) } returns Resultado.Exito(Datos.reserva())
        coEvery { repo.cancelarReserva() } returns Resultado.Error("No se pudo conectar con el servidor.")
        vm.seleccionarVuelo(Datos.vuelo1)
        vm.aceptarVueloSeleccionado { }

        vm.cancelarReserva()

        assertNotNull(vm.vueloAceptado)
        assertEquals("No se pudo conectar con el servidor.", vm.mensaje)
    }

    @Test fun cerrar_sesion_borra_todo_y_limpia_el_repositorio() {
        coEvery { repo.cerrarSesion() } just Runs
        coEvery { repo.reserva() } returns Resultado.Exito(Datos.reserva(true))
        vm.iniciarSesion(Datos.socio)
        vm.seleccionarVuelo(Datos.vuelo2)

        vm.cerrarSesion()

        assertEquals("", vm.correo)
        assertEquals(UsuarioApi.ROL_SOCIO, vm.rol)
        assertNull(vm.vueloSeleccionado)
        assertNull(vm.vueloAceptado)
        assertFalse(vm.consentimientoFirmado)
        coVerify { repo.cerrarSesion() }
    }
}
