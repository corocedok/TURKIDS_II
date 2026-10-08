@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.turcoespanolkids.ui.screens

import android.speech.tts.TextToSpeech
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.turcoespanolkids.model.LeccionUiState
import com.example.turcoespanolkids.model.Pregunta
import com.example.turcoespanolkids.navigation.Screen
import com.example.turcoespanolkids.viewmodel.LeccionViewModel
import com.example.turcoespanolkids.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.util.Locale

private val VerdeSuave = Color(0xFFA5D6A7)
private val NaranjaSuave = Color(0xFFFFCC80)
private val NaranjaTexto = Color(0xFFE65100)
private val VerdeTexto = Color(0xFF2E7D32)

/**
 * Devuelve una función que reproduce un texto con la voz del dispositivo.
 * El tercer parámetro indica si se deben mostrar avisos cuando falla
 * (true al tocar el botón, false en el audio automático).
 */
@Composable
private fun rememberHablar(): (String, Locale, Boolean) -> Unit {
    val context = LocalContext.current
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    val listo = remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val engine = TextToSpeech(context) { status ->
            listo.value = status == TextToSpeech.SUCCESS
        }
        tts.value = engine
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    return { texto, idioma, avisar ->
        val engine = tts.value
        if (engine == null || !listo.value) {
            if (avisar) {
                Toast.makeText(context, "El motor de voz aún no está listo", Toast.LENGTH_SHORT).show()
            }
        } else {
            val resultado = engine.setLanguage(idioma)
            if (resultado == TextToSpeech.LANG_MISSING_DATA ||
                resultado == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                if (avisar) {
                    Toast.makeText(
                        context,
                        "Falta instalar la voz: ${idioma.displayLanguage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                engine.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "turkids")
            }
        }
    }
}

/** La palabra que se muestra en pantalla es la que se pronuncia. */
private fun textoAudio(p: Pregunta): Pair<String, Locale> =
    if (p.mostrarTurco) p.palabra.tr to Locale.forLanguageTag("tr-TR")
    else p.palabra.es to Locale.forLanguageTag("es-US")

@Composable
fun LeccionScreen(
    unidadId: String,
    mainViewModel: MainViewModel,
    viewModel: LeccionViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val hablar = rememberHablar()

    LaunchedEffect(unidadId) { viewModel.cargarUnidad(unidadId) }

    LaunchedEffect(estado.finalizado) {
        if (estado.finalizado) {
            mainViewModel.navigateTo(
                screen = Screen.Resumen(estado.aciertos, estado.totalPreguntas),
                popUpToRoute = Screen.Home
            )
        }
    }

    // Reproducción automática al aparecer cada pregunta (sin avisos si falla)
    LaunchedEffect(estado.indiceActual, estado.preguntas) {
        val p = estado.preguntaActual ?: return@LaunchedEffect
        delay(800)
        val (texto, idioma) = textoAudio(p)
        hablar(texto, idioma, false)
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

    val mensaje: String? = when {
        estado.respondido && estado.esCorrecta -> "¡Muy bien! 🎉"
        estado.respondido -> "Esta es la respuesta. ¡Vamos con la siguiente! 🌟"
        estado.intentosFallidos > 0 -> "¡Casi! Escucha otra vez e inténtalo de nuevo 💪"
        else -> null
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
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = pregunta.palabra.emoji, fontSize = 72.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val (texto, idioma) = textoAudio(pregunta)
                    hablar(texto, idioma, true)
                },
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("🔊  Escuchar", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (pregunta.mostrarTurco)
                    "¿Cómo se dice \"${pregunta.palabra.tr}\" en español?"
                else
                    "¿Cómo se dice \"${pregunta.palabra.es}\" en turco?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

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

            mensaje?.let {
                Text(
                    text = it,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = if (estado.respondido && estado.esCorrecta) VerdeTexto else NaranjaTexto
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (estado.respondido) {
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
            opcion in estado.descartadas -> NaranjaSuave
            !estado.respondido -> MaterialTheme.colorScheme.surfaceVariant
            esCorrectaGlobal -> VerdeSuave
            esSeleccionada && !esCorrectaGlobal -> NaranjaSuave
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
        enabled = !estado.respondido && opcion !in estado.descartadas,
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