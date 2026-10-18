package com.example.myappnavigation.model

data class VueloModel(
    val id: Int,
    val base: String,
    val fechaIso: String,      // yyyy-MM-dd, se usa para filtrar con el calendario
    val fechaTexto: String,
    val horario: String,
    val aeronave: String,
    val piloto: String,
    val cupos: Int
)
