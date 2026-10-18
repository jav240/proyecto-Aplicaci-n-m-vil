package com.example.myappnavigation.model

data class VueloModel(
    val id: Int,
    val base: String,
    val fechaIso: String,
    val fechaTexto: String,
    val horario: String,
    val aeronave: String,
    val piloto: String,
    val cupos: Int
)
