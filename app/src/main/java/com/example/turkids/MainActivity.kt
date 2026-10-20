package com.example.turkids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.turkids.navigation.NavigationEvent
import com.example.turkids.navigation.Screen
import com.example.turkids.ui.screens.AcercaDeScreen
import com.example.turkids.ui.screens.HomeScreenAdaptativo
import com.example.turkids.ui.screens.LeccionScreen
import com.example.turkids.ui.screens.PracticaScreen
import com.example.turkids.ui.screens.RegistroScreen
import com.example.turkids.ui.screens.ResumenRegistroScreen
import com.example.turkids.ui.screens.ResumenScreen
import com.example.turkids.ui.theme.TurKidsTheme
import com.example.turkids.ui.utils.ObservarUbicacion
import com.example.turkids.viewmodel.MainViewModel
import com.example.turkids.viewmodel.ProgresoViewModel
import com.example.turkids.viewmodel.UbicacionViewModel
import com.example.turkids.viewmodel.UsuarioViewModel
import com.example.turkids.viewmodel.permisosUbicacion
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TurKidsTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = viewModel()
                val usuarioViewModel: UsuarioViewModel = viewModel()
                val progresoViewModel: ProgresoViewModel = viewModel()
                val ubicacionViewModel: UbicacionViewModel = viewModel()

                val solicitarUbicacion = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { ubicacionViewModel.registrarResultado() }

                ObservarUbicacion(ubicacionViewModel)

                LaunchedEffect(Unit) {
                    if (ubicacionViewModel.solicitudIniciada) return@LaunchedEffect
                    if (ubicacionViewModel.permisoConcedido()) {
                        ubicacionViewModel.registrarResultado()
                    } else {
                        ubicacionViewModel.iniciarSolicitud()
                        solicitarUbicacion.launch(permisosUbicacion)
                    }
                }

                LaunchedEffect(Unit) {
                    mainViewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(event.route.route) {
                                    event.popUpToRoute?.let {
                                        popUpTo(it.route) { inclusive = event.inclusive }
                                    }
                                    launchSingleTop = event.singleTop
                                }
                            }
                            is NavigationEvent.PopBackStack -> navController.popBackStack()
                            is NavigationEvent.NavigateUp -> navController.navigateUp()
                        }
                    }
                }

                NavHost(navController = navController, startDestination = Screen.Practica.route) {
                    composable(
                        route = Screen.Practica.route,
                        exitTransition = { fadeOut(tween(200)) }
                    ) {
                        PracticaScreen(
                            mainViewModel = mainViewModel,
                            progresoViewModel = progresoViewModel
                        )
                    }

                    composable(
                        route = Screen.Home.route,
                        exitTransition = { fadeOut(tween(200)) },
                        popEnterTransition = { fadeIn(tween(200)) }
                    ) {
                        HomeScreenAdaptativo(
                            mainViewModel = mainViewModel,
                            progresoViewModel = progresoViewModel,
                            ubicacionViewModel = ubicacionViewModel
                        )
                    }

                    composable(
                        route = Screen.AcercaDe.route,
                        enterTransition = { fadeIn(tween(250)) },
                        exitTransition = { fadeOut(tween(200)) }
                    ) {
                        AcercaDeScreen(mainViewModel = mainViewModel)
                    }

                    composable(Screen.Registro.route) {
                        RegistroScreen(
                            mainViewModel = mainViewModel,
                            usuarioViewModel = usuarioViewModel
                        )
                    }

                    composable(Screen.ResumenRegistro.route) {
                        ResumenRegistroScreen(
                            mainViewModel = mainViewModel,
                            usuarioViewModel = usuarioViewModel
                        )
                    }

                    composable(
                        route = Screen.Leccion.ROUTE_PATTERN,
                        arguments = listOf(navArgument(Screen.Leccion.ARG_UNIDAD_ID) {
                            type = NavType.StringType
                        }),
                        enterTransition = {
                            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(tween(300))
                        },
                        exitTransition = { fadeOut(tween(200)) },
                        popEnterTransition = { fadeIn(tween(200)) },
                        popExitTransition = {
                            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(tween(300))
                        }
                    ) { backStackEntry ->
                        val unidadId = backStackEntry.arguments
                            ?.getString(Screen.Leccion.ARG_UNIDAD_ID) ?: ""
                        LeccionScreen(
                            unidadId = unidadId,
                            mainViewModel = mainViewModel,
                            progresoViewModel = progresoViewModel
                        )
                    }

                    composable(
                        route = Screen.Resumen.ROUTE_PATTERN,
                        arguments = listOf(
                            navArgument(Screen.Resumen.ARG_ACIERTOS) { type = NavType.IntType },
                            navArgument(Screen.Resumen.ARG_TOTAL) { type = NavType.IntType }
                        ),
                        enterTransition = {
                            slideInVertically(initialOffsetY = { it }, animationSpec = tween(400)) + fadeIn(tween(400))
                        },
                        exitTransition = { fadeOut(tween(200)) }
                    ) { backStackEntry ->
                        val aciertos = backStackEntry.arguments
                            ?.getInt(Screen.Resumen.ARG_ACIERTOS) ?: 0
                        val total = backStackEntry.arguments
                            ?.getInt(Screen.Resumen.ARG_TOTAL) ?: 0
                        ResumenScreen(aciertos = aciertos, total = total, mainViewModel = mainViewModel)
                    }
                }
            }
        }
    }
}