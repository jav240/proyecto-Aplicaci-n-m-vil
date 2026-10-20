package com.example.myappnavigation.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.example.myappnavigation.model.VueloModel
import java.text.SimpleDateFormat
import java.util.Locale


object CalendarioNativo {

    fun agendarVuelo(context: Context, vuelo: VueloModel): Boolean {
        val partes = vuelo.horario.split("-").map { it.trim() }
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val inicio = formato.parse(vuelo.fechaIso + " " + partes[0])?.time ?: return false
        val fin = formato.parse(vuelo.fechaIso + " " + partes.getOrElse(1) { partes[0] })?.time ?: return false

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, "Vuelo recreativo CAPEBE (" + vuelo.horario + ")")
            putExtra(CalendarContract.Events.EVENT_LOCATION, "Aeródromo CAPEBE, base " + vuelo.base)
            putExtra(
                CalendarContract.Events.DESCRIPTION,
                "Base: " + vuelo.base + "\nAeronave: " + vuelo.aeronave + "\nPiloto: " + vuelo.piloto +
                    "\nRecuerda firmar el consentimiento y llevar tu pase con el código QR."
            )
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, inicio)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, fin)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
}
