package com.example.myappnavigation.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myappnavigation.data.remote.UsuarioApi


@Entity(tableName = "sesion")
data class SesionEntity(
    @PrimaryKey val id: Int = ID_UNICO,
    val token: String,
    val usuarioId: Long,
    val nombre: String,
    val email: String,
    val rol: String
) {
    companion object {
        const val ID_UNICO = 1
    }
}

fun SesionEntity.aUsuario() = UsuarioApi(id = usuarioId, nombre = nombre, email = email, rol = rol)

fun UsuarioApi.aSesionEntity(token: String) =
    SesionEntity(token = token, usuarioId = id, nombre = nombre, email = email, rol = rol)
