package com.example.myappnavigation.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import com.example.myappnavigation.ui.theme.NaranjaCapebe
import com.example.myappnavigation.ui.theme.VerdeCapebe
import com.example.myappnavigation.ui.viewmodel.SesionViewModel
import com.example.myappnavigation.util.CalendarioNativo
import com.example.myappnavigation.util.GeneradorQr

@Composable
fun PaseAbordaje(
    sesion: SesionViewModel,
    onVolverMenu: () -> Unit
) {
    val context = LocalContext.current
    val vuelo = sesion.vueloAceptado

    Scaffold(
        topBar = { Header("MI PASE DE ABORDAJE") },
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
            Text("Muestra este pase en el aeródromo", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (vuelo == null) {
                Text("Acepta un vuelo para ver tu pase.", style = MaterialTheme.typography.bodyLarge)
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
                                text = "Socio: " + sesion.correo,
                                color = AzulCapebe,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Base: " + vuelo.base, fontSize = 16.sp)
                            Text("Fecha: " + vuelo.fechaTexto + ", " + vuelo.horario, fontSize = 16.sp)
                            Text("Aeronave: " + vuelo.aeronave, fontSize = 16.sp)
                            Text(
                                text = "Consentimiento: " + (if (sesion.consentimientoFirmado) "FIRMADO" else "PENDIENTE"),
                                color = if (sesion.consentimientoFirmado) VerdeCapebe else NaranjaCapebe,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Código QR generado con los datos del socio, el vuelo y el consentimiento
                val contenidoQr = GeneradorQr.contenidoPase(sesion.correo, vuelo, sesion.consentimientoFirmado)
                val qr = remember(contenidoQr) { GeneradorQr.generar(contenidoQr).asImageBitmap() }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(220.dp)
                        .background(Color.White)
                        .border(4.dp, AzulCapebe)
                        .padding(8.dp)
                ) {
                    Image(
                        bitmap = qr,
                        contentDescription = "Código QR del pase de abordaje",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Presenta este código QR en el aeródromo.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Recurso nativo: agenda la fecha del vuelo en el calendario del dispositivo
                OutlinedButton(
                    onClick = {
                        if (!CalendarioNativo.agendarVuelo(context, vuelo)) {
                            Toast.makeText(
                                context,
                                "No hay una app de calendario en este dispositivo.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Agendar vuelo en mi calendario")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { onVolverMenu() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Volver al menú")
            }
        }
    }
}
