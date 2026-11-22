package com.example.myappnavigation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myappnavigation.ui.theme.AzulCapebe
import com.example.myappnavigation.ui.viewmodel.ClimaViewModel
import com.example.myappnavigation.ui.viewmodel.EstadoClima
import com.example.myappnavigation.util.DescripcionClima
import java.util.Locale

/** Clima actual de la base (API externa Open-Meteo, consumida con Retrofit). */
@Composable
fun TarjetaClima(base: String, viewModel: ClimaViewModel = viewModel()) {
    LaunchedEffect(base) { viewModel.cargar(base) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Clima en la base " + base,
                color = AzulCapebe,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            when (val estado = viewModel.estado) {
                is EstadoClima.Cargando -> CircularProgressIndicator()
                is EstadoClima.Fallo -> Text(
                    text = estado.mensaje,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
                is EstadoClima.Listo -> {
                    val clima = estado.clima
                    Text(
                        text = String.format(Locale.US, "%.1f °C · ", clima.temperatura) +
                            DescripcionClima.deCodigo(clima.codigo),
                        fontSize = 16.sp
                    )
                    Text(
                        text = String.format(Locale.US, "Viento: %.0f km/h", clima.vientoKmh),
                        fontSize = 16.sp
                    )
                    Text(
                        text = DescripcionClima.condicionParaVolar(clima) + " (referencial; decide el piloto)",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
