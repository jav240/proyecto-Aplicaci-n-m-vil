package com.example.myappnavigation.ui.screen

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun Consentimiento(
    sesion: SesionViewModel,
    onVolverMenu: () -> Unit
) {
    val vuelo = sesion.vueloAceptado
    val firmado = sesion.consentimientoFirmado


    val colorEstado by animateColorAsState(
        targetValue = if (firmado) VerdeCapebe else NaranjaCapebe,
        label = "colorConsentimiento"
    )

    Scaffold(
        topBar = { Header("CONSENTIMIENTO") },
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
            Text("Consentimiento antes de abordar", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (vuelo == null) {
                Text("Acepta un vuelo para ver tu consentimiento.", style = MaterialTheme.typography.bodyLarge)
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_consentimiento),
                            contentDescription = "Documento de consentimiento",
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Consentimiento de vuelo",
                                color = AzulCapebe,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Vuelo: " + vuelo.base + ", " + vuelo.fechaTexto + ", " + vuelo.horario,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Estado: " + (if (firmado) "FIRMADO" else "PENDIENTE"),
                                color = colorEstado,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Todo pasajero debe firmar un consentimiento legal de vuelo. " +
                                    "El peso se verifica en terreno antes de abordar.",
                                fontSize = 14.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = firmado,
                                    onCheckedChange = { sesion.onConsentimientoChanged(it) }
                                )
                                Text("Declaro haber leído y acepto el consentimiento", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { onVolverMenu() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Volver al menú")
            }
        }
    }
}
