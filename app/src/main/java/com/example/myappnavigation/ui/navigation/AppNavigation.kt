package com.example.myappnavigation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myappnavigation.ui.screen.Consentimiento
import com.example.myappnavigation.ui.screen.DetalleVuelo
import com.example.myappnavigation.ui.screen.Inicio
import com.example.myappnavigation.ui.screen.Login
import com.example.myappnavigation.ui.screen.PaseAbordaje
import com.example.myappnavigation.ui.screen.Principal
import com.example.myappnavigation.ui.screen.Register
import com.example.myappnavigation.ui.screen.VuelosDisponibles
import com.example.myappnavigation.ui.viewmodel.SesionViewModel

// definimos las rutas de navegación
object Routes {
    const val INICIO = "inicio"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PRINCIPAL = "principal"
    const val VUELOS = "vuelos"
    const val DETALLE = "detalle"
    const val CONSENTIMIENTO = "consentimiento"
    const val PASE = "pase"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Estado compartido entre pantallas: sesión, vuelo aceptado y consentimiento
    val sesion: SesionViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.INICIO     // pantalla de inicio
    ) {
        composable(Routes.INICIO) {
            Inicio(
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.LOGIN) {
            Login(
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = { correo ->
                    sesion.iniciarSesion(correo)
                    // navega al menú principal y limpia la pila de login
                    navController.navigate(Routes.PRINCIPAL) {
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
