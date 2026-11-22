package com.example.myappnavigation

import com.example.myappnavigation.data.remote.ReservaApi
import com.example.myappnavigation.data.remote.UsuarioApi
import com.example.myappnavigation.model.VueloModel

/** Datos de ejemplo compartidos por las pruebas. */
object Datos {
    val vuelo1 = VueloModel(1, "Tobalaba", "2026-11-07", "Sáb 7 nov 2026", "10:00 - 11:00", "Cessna 172 (CC-AAA)", "Piloto Uno", 2)
    val vuelo2 = VueloModel(2, "Talcahuano", "2026-11-07", "Sáb 7 nov 2026", "09:00 - 10:00", "Piper PA-28 (CC-BBB)", "Piloto Dos", 3)

    val socio = UsuarioApi(1, "Ana Pérez", "ana@correo.cl", UsuarioApi.ROL_SOCIO)
    val admin = UsuarioApi(2, "Admin Cashupin", "cashupinadmin@gmail.com", UsuarioApi.ROL_ADMIN)

    fun reserva(firmado: Boolean = false) = ReservaApi(vuelo1, firmado, "2026-11-01T10:00:00")
}
