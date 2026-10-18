package com.example.myappnavigation.util

import android.graphics.Bitmap
import android.graphics.Color
import com.example.myappnavigation.model.VueloModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter


object GeneradorQr {


    fun contenidoPase(correo: String, vuelo: VueloModel, consentimientoFirmado: Boolean): String =
        "CAPEBE|PASE" +
            "|socio=" + correo +
            "|vuelo=" + vuelo.id +
            "|base=" + vuelo.base +
            "|fecha=" + vuelo.fechaIso +
            "|horario=" + vuelo.horario +
                "|aeronave=" + vuelo.aeronave +
                "|emitido=" + java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()) +
                "|consentimiento=" + (if (consentimientoFirmado) "FIRMADO" else "PENDIENTE")

    fun generar(texto: String, tamano: Int = 512): Bitmap {
        val hints = mapOf(
            EncodeHintType.MARGIN to 1,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )
        val matriz = QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, tamano, tamano, hints)
        val pixeles = IntArray(tamano * tamano)
        for (y in 0 until tamano) {
            for (x in 0 until tamano) {
                pixeles[y * tamano + x] = if (matriz[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return Bitmap.createBitmap(pixeles, tamano, tamano, Bitmap.Config.ARGB_8888)
    }
}
