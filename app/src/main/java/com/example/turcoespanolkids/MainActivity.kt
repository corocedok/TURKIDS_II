package com.example.turcoespanolkids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.turcoespanolkids.navigation.Screen
import com.example.turcoespanolkids.ui.screens.HomeScreenAdaptativo
import com.example.turcoespanolkids.ui.screens.LeccionScreen
import com.example.turcoespanolkids.ui.screens.ResumenScreen
import com.example.turcoespanolkids.ui.theme.TurcoEspanolKidsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TurcoEspanolKidsTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Screen.Home.route) {
                    composable(
                        route = Screen.Home.route,
                        exitTransition = { fadeOut(tween(200)) },
                        popEnterTransition = { fadeIn(tween(200)) }
                    ) {
                        HomeScreenAdaptativo(navController = navController)
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
                        LeccionScreen(unidadId = unidadId, navController = navController)
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
                        ResumenScreen(aciertos = aciertos, total = total, navController = navController)
                    }
                }
            }
        }
    }
}