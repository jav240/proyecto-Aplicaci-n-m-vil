package com.example.myappnavigation

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myappnavigation.data.database.AppDatabase
import com.example.myappnavigation.data.local.entity.SesionEntity
import com.example.myappnavigation.data.local.entity.VueloEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RoomDaoTest {

    private lateinit var db: AppDatabase

    @Before fun abrir() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).build()
    }

    @After fun cerrar() = db.close()

    private fun vuelo(id: Int, fecha: String, horario: String = "10:00 - 11:00") =
        VueloEntity(id, "Tobalaba", fecha, fecha, horario, "Cessna 172", "Piloto", 2)

    @Test fun guardar_sesion_reemplaza_la_anterior() = runBlocking {
        val dao = db.sesionDao()
        dao.guardar(SesionEntity(token = "a", usuarioId = 1, nombre = "Ana", email = "ana@correo.cl", rol = "socio"))
        dao.guardar(SesionEntity(token = "b", usuarioId = 2, nombre = "Admin", email = "adm@correo.cl", rol = "administrador"))

        val sesion = dao.obtener()

        assertEquals("b", sesion?.token)
        assertEquals("administrador", sesion?.rol)
        assertEquals("adm@correo.cl", sesion?.email)
    }

    @Test fun borrar_sesion_la_elimina() = runBlocking {
        val dao = db.sesionDao()
        dao.guardar(SesionEntity(token = "a", usuarioId = 1, nombre = "Ana", email = "ana@correo.cl", rol = "socio"))

        dao.borrar()

        assertNull(dao.obtener())
    }

    @Test fun reemplazar_todos_deja_solo_la_lista_nueva_ordenada() = runBlocking {
        val dao = db.vueloDao()
        dao.insertar(listOf(vuelo(1, "2026-11-07")))

        dao.reemplazarTodos(listOf(vuelo(3, "2026-11-08"), vuelo(2, "2026-11-07")))

        assertEquals(listOf(2, 3), dao.todos().map { it.id })
    }

    @Test fun reemplazar_fecha_no_toca_otros_dias() = runBlocking {
        val dao = db.vueloDao()
        dao.insertar(listOf(vuelo(1, "2026-11-07"), vuelo(2, "2026-11-08")))

        dao.reemplazarFecha("2026-11-07", listOf(vuelo(3, "2026-11-07")))

        assertEquals(listOf(3, 2), dao.todos().map { it.id })
    }
}
