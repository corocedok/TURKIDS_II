package com.example.turcoespanolkids

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.turcoespanolkids.navigation.NavigationEvent
import com.example.turcoespanolkids.navigation.Screen
import com.example.turcoespanolkids.ui.screens.AcercaDeScreen
import com.example.turcoespanolkids.ui.screens.HomeScreenAdaptativo
import com.example.turcoespanolkids.ui.screens.LeccionScreen
import com.example.turcoespanolkids.ui.screens.PracticaScreen
import com.example.turcoespanolkids.ui.screens.RegistroScreen
import com.example.turcoespanolkids.ui.screens.ResumenRegistroScreen
import com.example.turcoespanolkids.ui.screens.ResumenScreen
import com.example.turcoespanolkids.ui.theme.TurcoEspanolKidsTheme
import com.example.turcoespanolkids.viewmodel.MainViewModel
import com.example.turcoespanolkids.viewmodel.ProgresoViewModel
import com.example.turcoespanolkids.viewmodel.UbicacionViewModel
import com.example.turcoespanolkids.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.collectLatest

private val permisosUbicacion = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TurcoEspanolKidsTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = viewModel()
                val usuarioViewModel: UsuarioViewModel = viewModel()
                val progresoViewModel: ProgresoViewModel = viewModel()
                val ubicacionViewModel: UbicacionViewModel = viewModel()
                val contexto = LocalContext.current

                val solicitarUbicacion = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { resultado ->
                    ubicacionViewModel.registrarResultado(resultado.values.any { it })
                }

                LaunchedEffect(Unit) {
                    if (ubicacionViewModel.solicitudIniciada) return@LaunchedEffect
                    val concedido = permisosUbicacion.any {
                        ContextCompat.checkSelfPermission(contexto, it) ==
                            PackageManager.PERMISSION_GRANTED
                    }
                    if (concedido) {
                        ubicacionViewModel.registrarResultado(true)
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
                            progresoViewModel = progresoViewModel
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