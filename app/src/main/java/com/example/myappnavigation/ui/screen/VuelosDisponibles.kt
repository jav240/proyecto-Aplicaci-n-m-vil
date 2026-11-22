package com.example.myappnavigation.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myappnavigation.R
import com.example.myappnavigation.ui.components.Footer
import com.example.myappnavigation.ui.components.Header
import com.example.myappnavigation.ui.components.TarjetaVuelo
import com.example.myappnavigation.ui.theme.CieloCapebe
import com.example.myappnavigation.ui.viewmodel.SesionViewModel
import com.example.myappnavigation.ui.viewmodel.VuelosViewModel
import java.util.Calendar

@Composable
fun VuelosDisponibles(
    sesion: SesionViewModel,
    onNavigateToDetalle: () -> Unit,
    viewModel: VuelosViewModel = viewModel()
) {
    val context = LocalContext.current
    // GET /vuelos al entrar a la pantalla
    LaunchedEffect(Unit) { viewModel.cargar() }

    Scaffold(
        topBar = { Header("VUELOS DISPONIBLES") },
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
            Text("Elige tu vuelo recreativo", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(12.dp))


            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_calendario),
                    contentDescription = "Calendario",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val hoy = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, day -> viewModel.onFechaSeleccionada(year, month, day) },
                        hoy.get(Calendar.YEAR),
                        hoy.get(Calendar.MONTH),
                        hoy.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text(text = "Filtrar por fecha")
                }
            }

            val fecha = viewModel.fechaFiltro
            if (fecha != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Vuelos del día " + fecha, style = MaterialTheme.typography.bodyLarge)
                OutlinedButton(onClick = { viewModel.quitarFiltro() }) {
                    Text(text = "Ver todos los vuelos")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            val vuelos = viewModel.vuelosFiltrados
            if (viewModel.cargando) {
                CircularProgressIndicator()
            }
            if (viewModel.mostrandoGuardados) {
                Text(
                    "Sin conexión: se muestran los últimos vuelos guardados. Los cupos pueden haber cambiado.",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedButton(onClick = { viewModel.cargar() }) { Text(text = "Reintentar") }
                Spacer(modifier = Modifier.height(8.dp))
            }
            viewModel.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
                OutlinedButton(onClick = { viewModel.cargar() }) { Text(text = "Reintentar") }
            }
            if (vuelos.isEmpty() && !viewModel.cargando && viewModel.error == null) {
                Text("Aún no hay vuelos para esta fecha.", style = MaterialTheme.typography.bodyLarge)
            }
            vuelos.forEach { vuelo ->
                TarjetaVuelo(
                    vuelo = vuelo,
                    aceptado = sesion.vueloAceptado?.id == vuelo.id,
                    onVerDetalle = {
                        sesion.seleccionarVuelo(vuelo)
                        onNavigateToDetalle()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
