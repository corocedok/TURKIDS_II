@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.turcoespanolkids.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.turcoespanolkids.navigation.Screen

/**
 * Pantalla de resumen: muestra el avance/finalización del módulo
 * mediante un resultado simulado (sin datos personales reales),
 * cumpliendo el punto 3.2 del caso: "Resultado básico de avance o finalización".
 */
@Composable
fun ResumenScreen(
    aciertos: Int,
    total: Int,
    navController: NavController
) {
    val estrellas = when {
        total == 0 -> 0
        aciertos == total -> 3
        aciertos >= total / 2 -> 2
        else -> 1
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Resumen de la lección") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "\u2B50".repeat(estrellas).ifEmpty { "\uD83D\uDD04" },
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¡Lección completada!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Respuestas correctas: $aciertos de $total",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Volver al inicio", fontSize = 18.sp)
            }
        }
    }
}
