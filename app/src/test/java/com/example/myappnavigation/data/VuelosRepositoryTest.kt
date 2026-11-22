package com.example.myappnavigation.data

import com.example.myappnavigation.Datos
import com.example.myappnavigation.data.local.dao.SesionDao
import com.example.myappnavigation.data.local.dao.VueloDao
import com.example.myappnavigation.data.local.entity.SesionEntity
import com.example.myappnavigation.data.local.entity.aEntidad
import com.example.myappnavigation.data.remote.AceptarRequest
import com.example.myappnavigation.data.remote.ConsentimientoRequest
import com.example.myappnavigation.data.remote.LoginRequest
import com.example.myappnavigation.data.remote.RegistroRequest
import com.example.myappnavigation.data.remote.ReservaAdminApi
import com.example.myappnavigation.data.remote.ReservaApi
import com.example.myappnavigation.data.remote.ReservaRespuesta
import com.example.myappnavigation.data.remote.SesionApi
import com.example.myappnavigation.data.remote.SocioApi
import com.example.myappnavigation.data.remote.UsuarioApi
import com.example.myappnavigation.data.remote.VuelosApi
import com.example.myappnavigation.data.repository.Resultado
import com.example.myappnavigation.data.repository.VuelosRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class VuelosRepositoryTest {

    private lateinit var api: VuelosApi
    private lateinit var sesionDao: SesionDao
    private lateinit var vueloDao: VueloDao
    private lateinit var repo: VuelosRepository

    @Before fun preparar() {
        api = mockk()
        sesionDao = mockk(relaxed = true)
        vueloDao = mockk(relaxed = true)
        repo = VuelosRepository(api, sesionDao, vueloDao)
    }

    private fun <T> error(codigo: Int, json: String): Response<T> =
        Response.error(codigo, json.toResponseBody("application/json".toMediaType()))

    private suspend fun iniciarSesion() {
        coEvery { api.login(any()) } returns Response.success(SesionApi("tok", Datos.socio))
        repo.login("ana@correo.cl", "clave123")
    }

    // ---------- Autenticación ----------

    @Test fun login_exitoso_guarda_token_y_usuario() = runTest {
        coEvery { api.login(LoginRequest("ana@correo.cl", "clave123")) } returns
            Response.success(SesionApi("tok", Datos.socio))

        val r = repo.login("  Ana@Correo.cl ", "clave123")

        assertEquals(Resultado.Exito(Datos.socio), r)
        assertEquals("tok", repo.token)
        assertEquals(Datos.socio, repo.usuario)
    }

    @Test fun login_con_credenciales_malas_devuelve_mensaje_del_servidor() = runTest {
        coEvery { api.login(any()) } returns error(401, """{"error":"Correo o contraseña incorrectos."}""")

        val r = repo.login("ana@correo.cl", "mala123")

        assertTrue(r is Resultado.Error)
        r as Resultado.Error
        assertEquals("Correo o contraseña incorrectos.", r.mensaje)
        assertEquals(401, r.codigo)
        assertNull(repo.token)
    }

    @Test fun register_no_deja_sesion_iniciada() = runTest {
        coEvery { api.register(RegistroRequest("Ana Pérez", "ana@correo.cl", "clave123")) } returns
            Response.success(201, SesionApi("tok", Datos.socio))

        val r = repo.register(" Ana Pérez ", "ANA@correo.cl", "clave123")

        assertEquals(Resultado.Exito(Datos.socio), r)
        assertNull(repo.token)
    }

    @Test fun register_con_campos_invalidos_devuelve_errores_por_campo() = runTest {
        coEvery { api.register(any()) } returns
            error(409, """{"error":"El correo ya está registrado.","campos":{"email":"Este correo ya está registrado."}}""")

        val r = repo.register("Ana Pérez", "ana@correo.cl", "clave123") as Resultado.Error

        assertEquals("El correo ya está registrado.", r.mensaje)
        assertEquals("Este correo ya está registrado.", r.campos["email"])
        assertEquals(409, r.codigo)
    }

    @Test fun sin_conexion_devuelve_error_amigable() = runTest {
        coEvery { api.login(any()) } throws IOException("sin red")

        val r = repo.login("ana@correo.cl", "clave123") as Resultado.Error

        assertTrue(r.mensaje.startsWith("No se pudo conectar"))
    }

    @Test fun error_sin_json_usa_codigo_http() = runTest {
        coEvery { api.login(any()) } returns error(500, "boom")

        val r = repo.login("ana@correo.cl", "clave123") as Resultado.Error

        assertEquals("Error del servidor (500).", r.mensaje)
    }

    @Test fun cerrar_sesion_borra_token_y_usuario() = runTest {
        iniciarSesion()
        repo.cerrarSesion()
        assertNull(repo.token)
        assertNull(repo.usuario)
    }


    // ---------- Sesión guardada en Room ----------

    @Test fun login_exitoso_guarda_token_rol_y_correo_en_room() = runTest {
        val guardada = slot<SesionEntity>()
        coEvery { sesionDao.guardar(capture(guardada)) } returns Unit
        coEvery { api.login(any()) } returns Response.success(SesionApi("tok", Datos.admin))

        repo.login("cashupinadmin@gmail.com", "clave123")

        assertEquals("tok", guardada.captured.token)
        assertEquals(UsuarioApi.ROL_ADMIN, guardada.captured.rol)
        assertEquals("cashupinadmin@gmail.com", guardada.captured.email)
    }

    @Test fun login_fallido_no_guarda_nada() = runTest {
        coEvery { api.login(any()) } returns error(401, """{"error":"Correo o contraseña incorrectos."}""")

        repo.login("ana@correo.cl", "mala123")

        coVerify(exactly = 0) { sesionDao.guardar(any()) }
    }

    @Test fun restaurar_sesion_recupera_token_y_usuario_guardados() = runTest {
        coEvery { sesionDao.obtener() } returns SesionEntity(
            token = "tok", usuarioId = 1, nombre = "Ana Pérez", email = "ana@correo.cl", rol = UsuarioApi.ROL_SOCIO
        )

        val usuario = repo.restaurarSesion()

        assertEquals(Datos.socio, usuario)
        assertEquals("tok", repo.token)
        coEvery { api.reserva("Bearer tok") } returns Response.success(ReservaRespuesta(null))
        assertEquals(Resultado.Exito<ReservaApi?>(null), repo.reserva())
    }

    @Test fun restaurar_sesion_sin_nada_guardado_devuelve_null() = runTest {
        coEvery { sesionDao.obtener() } returns null

        assertNull(repo.restaurarSesion())
        assertNull(repo.token)
    }

    @Test fun restaurar_sesion_si_room_falla_no_rompe() = runTest {
        coEvery { sesionDao.obtener() } throws IllegalStateException("base dañada")

        assertNull(repo.restaurarSesion())
    }

    @Test fun cerrar_sesion_borra_la_sesion_de_room() = runTest {
        iniciarSesion()

        repo.cerrarSesion()

        coVerify { sesionDao.borrar() }
    }

    // ---------- Vuelos ----------

    @Test fun vuelos_se_ordenan_por_fecha_y_horario() = runTest {
        coEvery { api.vuelos(null, null) } returns Response.success(listOf(Datos.vuelo1, Datos.vuelo2))

        val r = repo.vuelos() as Resultado.Exito

        assertEquals(listOf(Datos.vuelo2, Datos.vuelo1), r.datos) // 09:00 antes que 10:00
    }

    @Test fun vuelos_con_fecha_envia_el_filtro_y_ignora_texto_vacio() = runTest {
        coEvery { api.vuelos("2026-11-07", null) } returns Response.success(listOf(Datos.vuelo1))
        coEvery { api.vuelos(null, null) } returns Response.success(emptyList())

        assertEquals(Resultado.Exito(listOf(Datos.vuelo1)), repo.vuelos("2026-11-07"))
        assertEquals(Resultado.Exito(emptyList<Any>()), repo.vuelos(" "))
    }


    // ---------- Caché de vuelos en Room ----------

    @Test fun vuelos_exitosos_reemplazan_el_cache() = runTest {
        coEvery { api.vuelos(null, null) } returns Response.success(listOf(Datos.vuelo1, Datos.vuelo2))

        val r = repo.vuelos() as Resultado.Exito

        assertFalse(r.desdeCache)
        coVerify { vueloDao.reemplazarTodos(listOf(Datos.vuelo2.aEntidad(), Datos.vuelo1.aEntidad())) }
    }

    @Test fun vuelos_filtrados_solo_reemplazan_ese_dia_del_cache() = runTest {
        coEvery { api.vuelos("2026-11-07", null) } returns Response.success(listOf(Datos.vuelo1))

        repo.vuelos("2026-11-07")

        coVerify { vueloDao.reemplazarFecha("2026-11-07", listOf(Datos.vuelo1.aEntidad())) }
        coVerify(exactly = 0) { vueloDao.reemplazarTodos(any()) }
    }

    @Test fun sin_conexion_devuelve_los_vuelos_guardados() = runTest {
        coEvery { api.vuelos(null, null) } throws IOException("sin red")
        coEvery { vueloDao.todos() } returns listOf(Datos.vuelo1.aEntidad(), Datos.vuelo2.aEntidad())

        val r = repo.vuelos() as Resultado.Exito

        assertTrue(r.desdeCache)
        assertEquals(listOf(Datos.vuelo2, Datos.vuelo1), r.datos)
    }

    @Test fun sin_conexion_filtra_el_cache_por_fecha() = runTest {
        val otroDia = Datos.vuelo2.copy(id = 3, fechaIso = "2026-11-08")
        coEvery { api.vuelos("2026-11-07", null) } throws IOException("sin red")
        coEvery { vueloDao.todos() } returns listOf(Datos.vuelo1.aEntidad(), otroDia.aEntidad())

        val r = repo.vuelos("2026-11-07") as Resultado.Exito

        assertTrue(r.desdeCache)
        assertEquals(listOf(Datos.vuelo1), r.datos)
    }

    @Test fun servidor_caido_tambien_usa_el_cache() = runTest {
        coEvery { api.vuelos(null, null) } returns error(503, "caído")
        coEvery { vueloDao.todos() } returns listOf(Datos.vuelo1.aEntidad())

        val r = repo.vuelos() as Resultado.Exito

        assertTrue(r.desdeCache)
    }

    @Test fun sin_conexion_y_sin_cache_devuelve_el_error() = runTest {
        coEvery { api.vuelos(null, null) } throws IOException("sin red")
        coEvery { vueloDao.todos() } returns emptyList()

        val r = repo.vuelos() as Resultado.Error

        assertTrue(r.mensaje.startsWith("No se pudo conectar"))
    }

    @Test fun error_de_cliente_no_usa_el_cache() = runTest {
        coEvery { api.vuelos(null, null) } returns error(400, """{"error":"Fecha inválida."}""")
        coEvery { vueloDao.todos() } returns listOf(Datos.vuelo1.aEntidad())

        val r = repo.vuelos() as Resultado.Error

        assertEquals(400, r.codigo)
    }

    @Test fun si_room_falla_al_guardar_igual_devuelve_los_vuelos_del_servidor() = runTest {
        coEvery { api.vuelos(null, null) } returns Response.success(listOf(Datos.vuelo1))
        coEvery { vueloDao.reemplazarTodos(any()) } throws IllegalStateException("disco lleno")

        assertEquals(Resultado.Exito(listOf(Datos.vuelo1)), repo.vuelos())
    }

    // ---------- Reserva ----------

    @Test fun reserva_sin_sesion_no_llama_al_servidor() = runTest {
        val r = repo.reserva() as Resultado.Error

        assertEquals(401, r.codigo)
        coVerify(exactly = 0) { api.reserva(any()) }
    }

    @Test fun reserva_envia_token_bearer() = runTest {
        iniciarSesion()
        coEvery { api.reserva("Bearer tok") } returns Response.success(ReservaRespuesta(Datos.reserva()))

        assertEquals(Resultado.Exito<ReservaApi?>(Datos.reserva()), repo.reserva())
    }

    @Test fun reserva_inexistente_devuelve_null() = runTest {
        iniciarSesion()
        coEvery { api.reserva(any()) } returns Response.success(ReservaRespuesta(null))

        assertEquals(Resultado.Exito<ReservaApi?>(null), repo.reserva())
    }

    @Test fun aceptar_vuelo_envia_el_id() = runTest {
        iniciarSesion()
        coEvery { api.aceptar("Bearer tok", AceptarRequest(1)) } returns Response.success(ReservaRespuesta(Datos.reserva()))

        assertEquals(Resultado.Exito<ReservaApi?>(Datos.reserva()), repo.aceptarVuelo(1))
    }

    @Test fun aceptar_vuelo_sin_cupos_devuelve_error() = runTest {
        iniciarSesion()
        coEvery { api.aceptar(any(), any()) } returns error(409, """{"error":"Ese vuelo ya no tiene cupos."}""")

        val r = repo.aceptarVuelo(1) as Resultado.Error

        assertEquals("Ese vuelo ya no tiene cupos.", r.mensaje)
    }

    @Test fun firmar_consentimiento() = runTest {
        iniciarSesion()
        coEvery { api.consentimiento("Bearer tok", ConsentimientoRequest(true)) } returns
            Response.success(ReservaRespuesta(Datos.reserva(true)))

        val r = repo.firmarConsentimiento(true) as Resultado.Exito

        assertEquals(true, r.datos?.consentimientoFirmado)
    }

    @Test fun cancelar_reserva() = runTest {
        iniciarSesion()
        coEvery { api.cancelar("Bearer tok") } returns Response.success(Unit)

        assertEquals(Resultado.Exito(Unit), repo.cancelarReserva())
    }

    // ---------- Administrador ----------

    @Test fun admin_lista_socios_y_reservas() = runTest {
        coEvery { api.login(any()) } returns Response.success(SesionApi("adm", Datos.admin))
        repo.login("cashupinadmin@gmail.com", "clave123")
        val socio = SocioApi(1, "Ana Pérez", "ana@correo.cl", null)
        val reserva = ReservaAdminApi("Ana Pérez", "ana@correo.cl", Datos.vuelo1, true, null)
        coEvery { api.socios("Bearer adm") } returns Response.success(listOf(socio))
        coEvery { api.reservasAdmin("Bearer adm") } returns Response.success(listOf(reserva))

        assertEquals(Resultado.Exito(listOf(socio)), repo.socios())
        assertEquals(Resultado.Exito(listOf(reserva)), repo.reservasAdmin())
    }

    @Test fun admin_con_token_de_socio_recibe_403() = runTest {
        iniciarSesion()
        coEvery { api.socios(any()) } returns error(403, """{"error":"Solo para administradores."}""")

        val r = repo.socios() as Resultado.Error

        assertEquals(403, r.codigo)
        assertEquals("Solo para administradores.", r.mensaje)
    }
}
