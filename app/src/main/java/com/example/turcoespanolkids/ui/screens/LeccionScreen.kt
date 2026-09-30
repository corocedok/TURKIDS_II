@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.turcoespanolkids.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.turcoespanolkids.model.Pregunta
import com.example.turcoespanolkids.navigation.Screen
import com.example.turcoespanolkids.viewmodel.LeccionViewModel

/**
 * Pantalla de ejercicio: una pregunta a la vez, con botones grandes
 * y retroalimentación visual inmediata (verde = correcto, rojo = incorrecto),
 * inspirada en el patrón de interacción de Duolingo.
 */
@Composable
fun LeccionScreen(
    unidadId: String,
    navController: NavController,
    viewModel: LeccionViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()

    // Carga las preguntas la primera vez que se entra a la unidad
    androidx.compose.runtime.LaunchedEffect(unidadId) {
        viewModel.cargarUnidad(unidadId)
    }

    // Navega al resumen cuando la lección termina
    androidx.compose.runtime.LaunchedEffect(estado.finalizado) {
        if (estado.finalizado) {
            navController.navigate(
                Screen.Resumen(estado.aciertos, estado.totalPreguntas).route
            ) {
                popUpTo(Screen.Home.route)
            }
        }
    }

    val pregunta = estado.preguntaActual ?: return

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text(estado.tituloUnidad) })
                LinearProgressIndicator(
                    progress = { estado.progreso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(text = pregunta.palabra.emoji, fontSize = 72.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (pregunta.mostrarTurco)
                    "¿Cómo se dice \"${pregunta.palabra.tr}\" en español?"
                else
                    "¿Cómo se dice \"${pregunta.palabra.es}\" en turco?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            pregunta.opciones.forEach { opcion ->
                BotonOpcion(
                    texto = opcion,
                    estado = estado,
                    opcion = opcion,
                    onClick = { viewModel.seleccionarOpcion(opcion) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            if (estado.respondido) {
                Text(
                    text = if (estado.esCorrecta) "¡Muy bien! \uD83C\uDF89" else "Casi... \uD83D\uDCAA",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (estado.esCorrecta) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.siguientePregunta() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Continuar", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun BotonOpcion(
    texto: String,
    estado: com.example.turcoespanolkids.model.LeccionUiState,
    opcion: String,
    onClick: () -> Unit
) {
    val esSeleccionada = estado.opcionSeleccionada == opcion
    val esCorrectaGlobal = estado.preguntaActual?.respuestaCorrecta == opcion

    // Color de fondo según el estado de la respuesta
    val colorFondo by animateColorAsState(
        targetValue = when {
            !estado.respondido -> MaterialTheme.colorScheme.surfaceVariant
            esCorrectaGlobal -> Color(0xFFA5D6A7) // verde suave
            esSeleccionada && !esCorrectaGlobal -> Color(0xFFEF9A9A) // rojo suave
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        label = "colorBoton"
    )

    Surface(
        onClick = onClick,
        enabled = !estado.respondido,
        shape = RoundedCornerShape(20.dp),
        color = colorFondo,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = texto,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
