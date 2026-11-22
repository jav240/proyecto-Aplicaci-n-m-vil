package com.example.myappnavigation.data.repository

import com.example.myappnavigation.data.local.dao.SesionDao
import com.example.myappnavigation.data.local.dao.VueloDao
import com.example.myappnavigation.data.local.entity.aEntidad
import com.example.myappnavigation.data.local.entity.aModelo
import com.example.myappnavigation.data.local.entity.aSesionEntity
import com.example.myappnavigation.data.local.entity.aUsuario
import com.example.myappnavigation.data.remote.AceptarRequest
import com.example.myappnavigation.data.remote.ConsentimientoRequest
import com.example.myappnavigation.data.remote.LoginRequest
import com.example.myappnavigation.data.remote.RegistroRequest
import com.example.myappnavigation.data.remote.ReservaAdminApi
import com.example.myappnavigation.data.remote.ReservaApi
import com.example.myappnavigation.data.remote.SocioApi
import com.example.myappnavigation.data.remote.UsuarioApi
import com.example.myappnavigation.data.remote.VuelosApi
import com.example.myappnavigation.model.VueloModel
import kotlinx.coroutines.CancellationException
import retrofit2.Response


class VuelosRepository(
    private val api: VuelosApi,
    private val sesionDao: SesionDao,
    private val vueloDao: VueloDao
) {

    var token: String? = null
        private set

    var usuario: UsuarioApi? = null
        private set


    suspend fun register(nombre: String, email: String, password: String): Resultado<UsuarioApi> =
        Llamadas.ejecutar {
            api.register(RegistroRequest(nombre.trim(), email.trim().lowercase(), password))
        }.mapear { it.usuario }

    suspend fun login(email: String, password: String): Resultado<UsuarioApi> {
        val resultado = Llamadas.ejecutar {
            api.login(LoginRequest(email.trim().lowercase(), password))
        }
        return when (resultado) {
            is Resultado.Exito -> {
                token = resultado.datos.token
                usuario = resultado.datos.usuario
                local { sesionDao.guardar(resultado.datos.usuario.aSesionEntity(resultado.datos.token)) }
                Resultado.Exito(resultado.datos.usuario)
            }
            is Resultado.Error -> resultado
        }
    }


    suspend fun restaurarSesion(): UsuarioApi? {
        val guardada = local { sesionDao.obtener() } ?: return null
        token = guardada.token
        usuario = guardada.aUsuario()
        return usuario
    }

    suspend fun cerrarSesion() {
        token = null
        usuario = null
        local { sesionDao.borrar() }
    }



    suspend fun vuelos(fecha: String? = null): Resultado<List<VueloModel>> {
        val filtro = fecha?.takeIf { it.isNotBlank() }
        return when (val remoto = Llamadas.ejecutar { api.vuelos(filtro, null) }) {
            is Resultado.Exito -> {
                val ordenados = remoto.datos.sortedBy { it.fechaIso + it.horario }
                local {
                    val filas = ordenados.map { it.aEntidad() }
                    if (filtro == null) vueloDao.reemplazarTodos(filas) else vueloDao.reemplazarFecha(filtro, filas)
                }
                Resultado.Exito(ordenados)
            }
            is Resultado.Error -> if (remoto.sinRespuesta) vuelosGuardados(filtro) ?: remoto else remoto
        }
    }

    private suspend fun vuelosGuardados(fecha: String?): Resultado.Exito<List<VueloModel>>? {
        val guardados = local { vueloDao.todos() }?.takeIf { it.isNotEmpty() } ?: return null
        val lista = guardados.map { it.aModelo() }.let { todos ->
            if (fecha == null) todos else todos.filter { it.fechaIso == fecha }
        }
        return Resultado.Exito(lista.sortedBy { it.fechaIso + it.horario }, desdeCache = true)
    }

    // ---------- Reserva del socio (CRUD) ----------

    suspend fun reserva(): Resultado<ReservaApi?> =
        conSesion { api.reserva(it) }.mapear { it.reserva }

    /** Crea la reserva o, si ya existía, la cambia por otro vuelo. */
    suspend fun aceptarVuelo(vueloId: Int): Resultado<ReservaApi?> =
        conSesion { api.aceptar(it, AceptarRequest(vueloId)) }.mapear { it.reserva }

    suspend fun firmarConsentimiento(firmado: Boolean): Resultado<ReservaApi?> =
        conSesion { api.consentimiento(it, ConsentimientoRequest(firmado)) }.mapear { it.reserva }

    suspend fun cancelarReserva(): Resultado<Unit> = conSesion { api.cancelar(it) }

    // ---------- Administrador ----------

    suspend fun socios(): Resultado<List<SocioApi>> = conSesion { api.socios(it) }

    suspend fun reservasAdmin(): Resultado<List<ReservaAdminApi>> = conSesion { api.reservasAdmin(it) }

    private suspend fun <T> conSesion(llamada: suspend (String) -> Response<T>): Resultado<T> {
        val actual = token ?: return Resultado.Error("Debes iniciar sesión.", codigo = 401)
        return Llamadas.ejecutar { llamada("Bearer " + actual) }
    }


    private suspend fun <T> local(accion: suspend () -> T): T? =
        try {
            accion()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
}
