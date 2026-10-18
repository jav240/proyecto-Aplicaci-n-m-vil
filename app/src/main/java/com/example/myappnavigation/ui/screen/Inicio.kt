package com.example.myappnavigation.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myappnavigation.R
import com.example.myappnavigation.ui.components.Footer
import com.example.myappnavigation.ui.components.Header
import com.example.myappnavigation.ui.theme.AzulCapebe
import com.example.myappnavigation.ui.theme.CieloCapebe
import com.example.myappnavigation.ui.theme.NaranjaCapebe

@Composable
fun Inicio(onNavigateToLogin: () -> Unit) {
    val context = LocalContext.current


    var logoVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { logoVisible = true }

    Scaffold(
        topBar = { Header(title = "APLICACIÓN VUELOS") },
        bottomBar = { Footer() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CieloCapebe),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                AnimatedVisibility(
                    visible = logoVisible,
                    enter = fadeIn() + scaleIn()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(220.dp)
                            .shadow(12.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(AzulCapebe)
                            .border(4.dp, NaranjaCapebe, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.avion),
                            contentDescription = "Avioneta del Club Aéreo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(120.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Vuelos recreativos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AzulCapebe,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Club Aéreo del Personal de BancoEstado",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AzulCapebe
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "Bienvenid@ a CAPEBE", Toast.LENGTH_SHORT).show()
                        onNavigateToLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(56.dp)
                        .shadow(8.dp, shape = CircleShape)
                ) {
                    Text(
                        text = "Ingresar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InicioPreview() {
    Inicio(onNavigateToLogin = {})
}
