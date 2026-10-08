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

    fun cargarUnidad(unidadId: String) {
        val unidad = BancoDeContenido.unidades.firstOrNull { it.id == unidadId } ?: return
        _estado.update {
            LeccionUiState(
                tituloUnidad = unidad.titulo,
                preguntas = BancoDeContenido.generarPreguntas(unidad)
            )
        }
    }

    /** Criterio del especialista: el error no castiga. El primer error da una segunda oportunidad. */
    fun seleccionarOpcion(opcion: String) {
        val actual = _estado.value
        if (actual.respondido || opcion in actual.descartadas) return

        val correcta = opcion == actual.preguntaActual?.respuestaCorrecta
        _estado.update {
            when {
                correcta -> it.copy(
                    opcionSeleccionada = opcion,
                    respondido = true,
                    esCorrecta = true,
                    aciertos = it.aciertos + 1
                )
                it.intentosFallidos == 0 -> it.copy(
                    descartadas = it.descartadas + opcion,
                    intentosFallidos = 1
                )
                else -> it.copy(
                    opcionSeleccionada = opcion,
                    respondido = true,
                    esCorrecta = false
                )
            }
        }
    }

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
                    descartadas = emptySet(),
                    intentosFallidos = 0,
                    respondido = false,
                    esCorrecta = false
                )
            }
        }
    }
}