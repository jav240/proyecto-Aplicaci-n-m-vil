package com.example.myappnavigation.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.myappnavigation.model.VueloModel


object CorreoNativo {
    fun abrirBorradorConfirmacion(context: Context, destinatario: String, vuelo: VueloModel): Boolean {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
            putExtra(Intent.EXTRA_SUBJECT, "Confirmación de vuelo recreativo CAPEBE")
            putExtra(
                Intent.EXTRA_TEXT,
                "Has aceptado el vuelo del " + vuelo.fechaTexto + ", " + vuelo.horario +
                    " en la base " + vuelo.base + ".\nAeronave: " + vuelo.aeronave +
                        "\nRecuerda firmar el consentimiento de vuelo antes de abordar." +
                        "\nGracias por volar con CAPEBE."
            )
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
}
