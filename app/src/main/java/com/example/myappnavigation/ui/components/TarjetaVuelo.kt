package com.example.myappnavigation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myappnavigation.R
import com.example.myappnavigation.model.VueloModel
import com.example.myappnavigation.ui.theme.AzulCapebe
import com.example.myappnavigation.ui.theme.NaranjaCapebe
import com.example.myappnavigation.ui.theme.VerdeCapebe

@Composable
fun TarjetaVuelo(
    vuelo: VueloModel,
    aceptado: Boolean,
    onVerDetalle: () -> Unit
) {

    var expandida by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expandida = !expandida },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = R.drawable.ic_vuelo),
                contentDescription = "Avioneta",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Base " + vuelo.base,
                    color = AzulCapebe,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = vuelo.fechaTexto + " · " + vuelo.horario, fontSize = 16.sp)
                Text(text = "Aeronave: " + vuelo.aeronave, fontSize = 16.sp)
                Text(
                    text = if (vuelo.cupos > 0) "Cupos disponibles: " + vuelo.cupos else "Sin cupos",
                    color = NaranjaCapebe,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (aceptado) {
                    Text(
                        text = "Vuelo aceptado",
                        color = VerdeCapebe,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (expandida) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Piloto al mando: " + vuelo.piloto, fontSize = 16.sp)
                    Text(text = "Vuelo gratuito para socios y su familia directa.", fontSize = 14.sp)
                } else {
                    Text(text = "Toca para ver más detalles", fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onVerDetalle() }) {
                    Text(text = "Ver detalle")
                }
            }
        }
    }
}
