package com.example.myappnavigation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myappnavigation.R
import com.example.myappnavigation.ui.components.Footer
import com.example.myappnavigation.ui.components.Header
import com.example.myappnavigation.ui.theme.AzulCapebe
import com.example.myappnavigation.ui.theme.CieloCapebe
import com.example.myappnavigation.ui.theme.NaranjaCapebe
import com.example.myappnavigation.ui.viewmodel.SesionViewModel

@Composable
fun Principal(
    sesion: SesionViewModel,
    onNavigateToVuelos: () -> Unit,
    onNavigateToConsentimiento: () -> Unit,
    onNavigateToPase: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = { Header("MENÚ DEL SOCIO") },
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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(AzulCapebe)
                    .border(3.dp, NaranjaCapebe, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avion),
                    contentDescription = "Avioneta del Club Aéreo",
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("¡Bienvenid@, " + sesion.correo + "!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))


            Button(onClick = { onNavigateToVuelos() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Reservar un vuelo")
            }
            Spacer(modifier = Modifier.height(12.dp))


            Button(
                onClick = { onNavigateToPase() },
                enabled = sesion.tieneVuelo,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Ver mi pase de abordaje")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onNavigateToConsentimiento() },
                enabled = sesion.tieneVuelo,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Mi consentimiento de vuelo")
            }
            if (!sesion.tieneVuelo) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Acepta un vuelo para habilitar tu pase y tu consentimiento.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = { onLogout() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Cerrar sesión")
            }
        }
    }
}
