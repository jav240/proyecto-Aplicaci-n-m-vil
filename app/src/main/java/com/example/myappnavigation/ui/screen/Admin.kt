package com.example.myappnavigation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myappnavigation.ui.components.Footer
import com.example.myappnavigation.ui.components.Header
import com.example.myappnavigation.ui.theme.AzulCapebe
import com.example.myappnavigation.ui.theme.CieloCapebe
import com.example.myappnavigation.ui.theme.NaranjaCapebe
import com.example.myappnavigation.ui.viewmodel.AdminViewModel
import com.example.myappnavigation.ui.viewmodel.SesionViewModel

@Composable
fun Admin(
    sesion: SesionViewModel,
    onLogout: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.cargar() }

    val vuelos = viewModel.vuelos

    Scaffold(
        topBar = { Header("PANEL DE ADMINISTRADOR") },
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
            verticalArrangement = Arrangement.Top
        ) {
            Text("¡Bienvenid@, administrador!", style = MaterialTheme.typography.headlineMedium)
            Text(sesion.correo, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // ---- Socios registrados ----
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Socios registrados: " + viewModel.socios.size,
                        color = AzulCapebe,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (viewModel.socios.isEmpty()) {
                        Text("Aún no hay socios registrados.", style = MaterialTheme.typography.bodyMedium)
                    }
                    viewModel.socios.forEach { socio ->
                        Text(socio.nombre, fontWeight = FontWeight.Bold)
                        Text(socio.email, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            viewModel.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }


            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Reservas: " + viewModel.reservas.size,
                        color = AzulCapebe,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (viewModel.reservas.isEmpty()) {
                        Text("Aún no hay reservas.", style = MaterialTheme.typography.bodyMedium)
                    }
                    viewModel.reservas.forEach { reserva ->
                        Text(reserva.socio, fontWeight = FontWeight.Bold)
                        Text(
                            "Base " + reserva.vuelo.base + " · " + reserva.vuelo.fechaTexto + " · " + reserva.vuelo.horario,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Consentimiento: " + (if (reserva.consentimientoFirmado) "FIRMADO" else "PENDIENTE"),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // ---- Vuelos programados ----
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Vuelos programados: " + vuelos.size,
                        color = AzulCapebe,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    vuelos.forEach { vuelo ->
                        Text("Base " + vuelo.base, fontWeight = FontWeight.Bold)
                        Text(vuelo.fechaTexto + " · " + vuelo.horario, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            vuelo.aeronave + " · " + vuelo.piloto,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            if (vuelo.cupos > 0) "Cupos disponibles: " + vuelo.cupos else "Sin cupos",
                            color = NaranjaCapebe,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = { onLogout() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Cerrar sesión")
            }
        }
    }
}
