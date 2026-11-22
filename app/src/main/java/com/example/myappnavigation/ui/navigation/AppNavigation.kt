package com.example.myappnavigation.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import com.example.myappnavigation.ui.screen.Admin
import com.example.myappnavigation.ui.screen.Consentimiento
import com.example.myappnavigation.ui.screen.DetalleVuelo
import com.example.myappnavigation.ui.screen.Inicio
import com.example.myappnavigation.ui.screen.Login
import com.example.myappnavigation.ui.screen.PaseAbordaje
import com.example.myappnavigation.ui.screen.Principal
import com.example.myappnavigation.ui.screen.Register
import com.example.myappnavigation.ui.screen.VuelosDisponibles
import com.example.myappnavigation.ui.theme.CieloCapebe
import com.example.myappnavigation.ui.viewmodel.SesionViewModel


object Routes {
    const val INICIO = "inicio"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PRINCIPAL = "principal"
    const val ADMIN = "admin"
    const val VUELOS = "vuelos"
    const val DETALLE = "detalle"
    const val CONSENTIMIENTO = "consentimiento"
    const val PASE = "pase"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val sesion: SesionViewModel = viewModel()
    val context = LocalContext.current


    LaunchedEffect(Unit) { sesion.restaurarSesion() }

    if (sesion.restaurando) {
        Box(
            modifier = Modifier.fillMaxSize().background(CieloCapebe),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }
        return
    }


    val destinoInicial = remember {
        when {
            !sesion.haySesion -> Routes.INICIO
            sesion.esAdmin -> Routes.ADMIN
            else -> Routes.PRINCIPAL
        }
    }


    LaunchedEffect(sesion.sesionExpirada) {
        if (sesion.sesionExpirada) {
            navController.currentBackStackEntryFlow.first()
            Toast.makeText(context, "Tu sesión expiró. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
            navController.navigate(Routes.LOGIN) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
            sesion.sesionExpiradaAtendida()
        }
    }

    NavHost(
        navController = navController,
        startDestination = destinoInicial
    ) {
        composable(Routes.INICIO) {
            Inicio(
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.LOGIN) {
            Login(
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = { usuario ->
                    sesion.iniciarSesion(usuario)
                    // según el rol que entrega el backend: administrador -> panel admin, socio -> menú
                    val destino = if (sesion.esAdmin) Routes.ADMIN else Routes.PRINCIPAL
                    // navega a la pantalla del rol y limpia la pila de login
                    navController.navigate(destino) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            Register(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        // limpia la pila para no acumular pantallas login activas
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PRINCIPAL) {
            Principal(
                sesion = sesion,
                onNavigateToVuelos = { navController.navigate(Routes.VUELOS) },
                onNavigateToConsentimiento = { navController.navigate(Routes.CONSENTIMIENTO) },
                onNavigateToPase = { navController.navigate(Routes.PASE) },
                onLogout = {
                    sesion.cerrarSesion()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.PRINCIPAL) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ADMIN) {
            Admin(
                sesion = sesion,
                onLogout = {
                    sesion.cerrarSesion()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ADMIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.VUELOS) {
            VuelosDisponibles(
                sesion = sesion,
                onNavigateToDetalle = { navController.navigate(Routes.DETALLE) }
            )
        }

        composable(Routes.DETALLE) {
            DetalleVuelo(
                sesion = sesion,
                // al aceptar el vuelo, lleva al consentimiento
                onVueloAceptado = { navController.navigate(Routes.CONSENTIMIENTO) }
            )
        }

        composable(Routes.CONSENTIMIENTO) {
            Consentimiento(
                sesion = sesion,
                onVolverMenu = {
                    navController.navigate(Routes.PRINCIPAL) {
                        popUpTo(Routes.PRINCIPAL) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PASE) {
            PaseAbordaje(
                sesion = sesion,
                onVolverMenu = {
                    navController.navigate(Routes.PRINCIPAL) {
                        popUpTo(Routes.PRINCIPAL) { inclusive = true }
                    }
                }
            )
        }
    }
}
