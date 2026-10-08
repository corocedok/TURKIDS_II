@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.turcoespanolkids.ui.screens

import com.example.turcoespanolkids.viewmodel.MainViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.turcoespanolkids.model.LeccionUiState
import com.example.turcoespanolkids.navigation.Screen
import com.example.turcoespanolkids.viewmodel.LeccionViewModel
import kotlinx.coroutines.delay

@Composable
fun LeccionScreen(
    unidadId: String,
    mainViewModel: MainViewModel,
    viewModel: LeccionViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(unidadId) { viewModel.cargarUnidad(unidadId) }

    LaunchedEffect(estado.finalizado) {
        if (estado.finalizado) {
            mainViewModel.navigateTo(
                screen = Screen.Resumen(estado.aciertos, estado.totalPreguntas),
                popUpToRoute = Screen.Home
            )
        }
    }

    val pregunta = estado.preguntaActual ?: return

    var opcionesVisibles by remember(estado.indiceActual) { mutableStateOf(0) }
    LaunchedEffect(estado.indiceActual) {
        opcionesVisibles = 0
        pregunta.opciones.indices.forEach { index ->
            delay(90L)
            opcionesVisibles = index + 1
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text(estado.tituloUnidad) })
                LinearProgressIndicator(
                    progress = { estado.progreso },
                    modifier = Modifier.fillMaxWidth().height(8.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
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

            pregunta.opciones.forEachIndexed { index, opcion ->
                AnimatedVisibility(
                    visible = index < opcionesVisibles,
                    enter = fadeIn(tween(300)) + slideInHorizontally(
                        initialOffsetX = { 60 },
                        animationSpec = tween(300)
                    )
                ) {
                    BotonOpcion(
                        texto = opcion,
                        estado = estado,
                        opcion = opcion,
                        onClick = { viewModel.seleccionarOpcion(opcion) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            if (estado.respondido) {
                Text(
                    text = if (estado.esCorrecta) "¡Muy bien! 🎉" else "Casi... 💪",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (estado.esCorrecta) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.siguientePregunta() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
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
    estado: LeccionUiState,
    opcion: String,
    onClick: () -> Unit
) {
    val esSeleccionada = estado.opcionSeleccionada == opcion
    val esCorrectaGlobal = estado.preguntaActual?.respuestaCorrecta == opcion

    val colorFondo by animateColorAsState(
        targetValue = when {
            !estado.respondido -> MaterialTheme.colorScheme.surfaceVariant
            esCorrectaGlobal -> Color(0xFFA5D6A7)
            esSeleccionada && !esCorrectaGlobal -> Color(0xFFEF9A9A)
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        label = "colorBoton"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "escalaBoton"
    )

    Surface(
        onClick = onClick,
        enabled = !estado.respondido,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(20.dp),
        color = colorFondo,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().height(64.dp).scale(scale)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text = texto, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }
    }
}