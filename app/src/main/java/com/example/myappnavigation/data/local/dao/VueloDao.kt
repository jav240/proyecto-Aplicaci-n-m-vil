package com.example.myappnavigation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.myappnavigation.data.local.entity.VueloEntity

@Dao
abstract class VueloDao {

    @Query("SELECT * FROM vuelos ORDER BY fechaIso, horario")
    abstract suspend fun todos(): List<VueloEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(vuelos: List<VueloEntity>)

    @Query("DELETE FROM vuelos")
    abstract suspend fun borrarTodos()

    @Query("DELETE FROM vuelos WHERE fechaIso = :fecha")
    abstract suspend fun borrarPorFecha(fecha: String)


    @Transaction
    open suspend fun reemplazarTodos(vuelos: List<VueloEntity>) {
        borrarTodos()
        insertar(vuelos)
    }


    @Transaction
    open suspend fun reemplazarFecha(fecha: String, vuelos: List<VueloEntity>) {
        borrarPorFecha(fecha)
        insertar(vuelos)
    }
}
