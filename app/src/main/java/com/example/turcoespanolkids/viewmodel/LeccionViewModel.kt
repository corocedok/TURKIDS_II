package com.example.turcoespanolkids.viewmodel

import androidx.lifecycle.ViewModel
import com.example.turcoespanolkids.model.BancoDeContenido
import com.example.turcoespanolkids.model.LeccionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class LeccionViewModel : ViewModel() {

    private val _estado = MutableStateFlow(LeccionUiState())
    val estado: StateFlow<LeccionUiState> = _estado

    /** Carga las preguntas de la unidad seleccionada. */
    fun cargarUnidad(unidadId: String) {
        val unidad = BancoDeContenido.unidades.firstOrNull { it.id == unidadId } ?: return
        _estado.update {
            LeccionUiState(
                tituloUnidad = unidad.titulo,
                preguntas = BancoDeContenido.generarPreguntas(unidad)
            )
        }
    }

    /** Registra la opción elegida y evalúa si es correcta. */
    fun seleccionarOpcion(opcion: String) {
        val actual = _estado.value
        if (actual.respondido) return // evita doble respuesta

        val esCorrecta = opcion == actual.preguntaActual?.respuestaCorrecta
        _estado.update {
            it.copy(
                opcionSeleccionada = opcion,
                respondido = true,
                esCorrecta = esCorrecta,
                aciertos = if (esCorrecta) it.aciertos + 1 else it.aciertos
            )
        }
    }

    /** Avanza a la siguiente pregunta o marca la lección como finalizada. */
    fun siguientePregunta() {
        val actual = _estado.value
        val siguienteIndice = actual.indiceActual + 1

        if (siguienteIndice >= actual.preguntas.size) {
            _estado.update { it.copy(finalizado = true) }
        } else {
            _estado.update {
                it.copy(
                    indiceActual = siguienteIndice,
                    opcionSeleccionada = null,
                    respondido = false,
                    esCorrecta = false
                )
            }
        }
    }
}
