package com.example.myappnavigation.util

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.myappnavigation.model.VueloModel
import java.io.ByteArrayOutputStream
import java.util.Properties
import javax.activation.DataHandler
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


object CorreoSmtp {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val principal = Handler(Looper.getMainLooper())

    fun configurado(): Boolean =
        ConfigCorreo.REMITENTE.isNotBlank() && ConfigCorreo.CLAVE_APP.isNotBlank()


    fun enviarConfirmacion(
        destinatario: String,
        vuelo: VueloModel,
        consentimientoFirmado: Boolean,
        alTerminar: (Boolean) -> Unit
    ) {
        scope.launch {
            val ok = try {
                val qr = GeneradorQr.generar(
                    GeneradorQr.contenidoPase(destinatario, vuelo, consentimientoFirmado)
                )
                enviar(destinatario, vuelo, qr)
                true
            } catch (e: Exception) {
                Log.e("CorreoSmtp", "No se pudo enviar el correo", e)
                false
            }
            principal.post { alTerminar(ok) }
        }
    }

    private fun enviar(destinatario: String, vuelo: VueloModel, qr: Bitmap) {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", ConfigCorreo.HOST)
            put("mail.smtp.port", ConfigCorreo.PUERTO)
            put("mail.smtp.ssl.trust", ConfigCorreo.HOST)
            put("mail.smtp.connectiontimeout", "15000")
            put("mail.smtp.timeout", "15000")
        }
        val sesion = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication() =
                PasswordAuthentication(ConfigCorreo.REMITENTE, ConfigCorreo.CLAVE_APP)
        })

        val texto = "Has aceptado el vuelo recreativo de CAPEBE.\n\n" +
            "Base: " + vuelo.base + "\n" +
            "Fecha: " + vuelo.fechaTexto + ", " + vuelo.horario + "\n" +
            "Aeronave: " + vuelo.aeronave + "\n" +
            "Piloto al mando: " + vuelo.piloto + "\n\n" +
            "Recuerda firmar el consentimiento de vuelo antes de abordar.\n" +
            "Adjuntamos tu código QR: preséntalo en el aeródromo."

        val cuerpo = MimeBodyPart().apply { setText(texto, "UTF-8") }

        val png = ByteArrayOutputStream().also { qr.compress(Bitmap.CompressFormat.PNG, 100, it) }
        val adjunto = MimeBodyPart().apply {
            dataHandler = DataHandler(ByteArrayDataSource(png.toByteArray(), "image/png"))
            fileName = "pase-qr-vuelo-" + vuelo.id + ".png"
        }

        val mensaje = MimeMessage(sesion).apply {
            setFrom(InternetAddress(ConfigCorreo.REMITENTE, "CAPEBE", "UTF-8"))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario))
            setSubject("Confirmación de vuelo recreativo CAPEBE", "UTF-8")
            setContent(MimeMultipart("mixed").apply {
                addBodyPart(cuerpo)
                addBodyPart(adjunto)
            })
        }
        Transport.send(mensaje)
    }
}
