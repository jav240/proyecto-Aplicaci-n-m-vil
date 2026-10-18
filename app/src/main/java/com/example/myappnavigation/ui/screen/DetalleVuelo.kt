package com.example.myappnavigation.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myappnavigation.R
import com.example.myappnavigation.ui.components.Footer
import com.example.myappnavigation.ui.components.Header
import com.example.myappnavigation.ui.theme.AzulCapebe
import com.example.myappnavigation.ui.theme.CieloCapebe
import com.example.myappnavigation.ui.viewmodel.SesionViewModel
import com.example.myappnavigation.util.CorreoNativo
import com.example.myappnavigation.util.CorreoSmtp
import androidx.compose.material3.ButtonDefaults
import com.example.myappnavigation.ui.theme.VerdeCapebe

@Composable
fun DetalleVuelo(
    sesion: SesionViewModel,
    onVueloAceptado: () -> Unit
) {
    val context = LocalContext.current
    val vuelo = sesion.vueloSeleccionado

    Scaffold(
        topBar = { Header("DETALLE DEL VUELO") },
        bottomBar = { Footer() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CieloCapebe)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Vuelo recreativo gratuito", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (vuelo == null) {
                Text("Primero elige un vuelo en la lista.", style = MaterialTheme.typography.bodyLarge)
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_vuelo),
                            contentDescription = "Avioneta",
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Base " + vuelo.base,
                                color = AzulCapebe,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Fecha: " + vuelo.fechaTexto, fontSize = 16.sp)
                            Text("Bloque: " + vuelo.horario, fontSize = 16.sp)
                            Text("Aeronave: " + vuelo.aeronave, fontSize = 16.sp)
                            Text("Piloto al mando: " + vuelo.piloto, fontSize = 16.sp)
                            Text("Cupos disponibles: " + vuelo.cupos, fontSize = 16.sp)
                            Text(
                                "Pueden volar el socio y su familia directa (cónyuge e hijos).",
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Al aceptar el vuelo te llegará la confirmación a tu correo.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (sesion.aceptarVueloSeleccionado()) {
                            val correo = sesion.correo
                            val firmado = sesion.consentimientoFirmado
                            if (CorreoSmtp.configurado()) {

                                Toast.makeText(context, "¡Vuelo aceptado! Enviando confirmación a tu correo...", Toast.LENGTH_SHORT).show()
                                CorreoSmtp.enviarConfirmacion(correo, vuelo, firmado) { ok ->
                                    val aviso = if (ok) {
                                        "Confirmación enviada a " + correo
                                    } else {
                                        "No se pudo enviar el correo. Revisa tu conexión."
                                    }
                                    Toast.makeText(context, aviso, Toast.LENGTH_LONG).show()
                                }
                            } else {

                                val abierto = CorreoNativo.abrirBorradorConfirmacion(context, correo, vuelo)
                                val mensaje = if (abierto) {
                                    "¡Vuelo aceptado! Revisa tu correo para enviar la confirmación."
                                } else {
                                    "¡Vuelo aceptado! No hay una app de correo en este dispositivo."
                                }
                                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                            }
                            onVueloAceptado()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeCapebe),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Aceptar este vuelo")
                }
            }
        }
    }
}
