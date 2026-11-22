package com.example.myappnavigation.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myappnavigation.model.VueloModel


@Entity(tableName = "vuelos")
data class VueloEntity(
    @PrimaryKey val id: Int,
    val base: String,
    val fechaIso: String,
    val fechaTexto: String,
    val horario: String,
    val aeronave: String,
    val piloto: String,
    val cupos: Int
)

fun VueloEntity.aModelo() = VueloModel(id, base, fechaIso, fechaTexto, horario, aeronave, piloto, cupos)

fun VueloModel.aEntidad() = VueloEntity(id, base, fechaIso, fechaTexto, horario, aeronave, piloto, cupos)
