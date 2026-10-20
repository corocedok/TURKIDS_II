package com.example.turcoespanolkids.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.turcoespanolkids.model.BancoDeContenido
import com.example.turcoespanolkids.model.IdiomaPractica
import com.example.turcoespanolkids.model.LeccionGuardada
import com.example.turcoespanolkids.model.LeccionUiState
import com.example.turcoespanolkids.repository.ProgresoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class LeccionViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ProgresoRepository(application)

    private val _estado = MutableStateFlow(LeccionUiState())
    val estado: StateFlow<LeccionUiState> = _estado

    private var perfilId = ""
    private var unidadId = ""
    private var idioma = IdiomaPractica.TURCO
    private var semilla = 0L

    fun cargarUnidad(unidadId: String, idioma: IdiomaPractica, perfilId: String) {
        if (unidadId == this.unidadId && idioma == this.idioma && perfilId == this.perfilId) return
        val unidad = BancoDeContenido.unidades.firstOrNull { it.id == unidadId } ?: return

        val guardada = repositorio.leccionGuardada(perfilId)
            ?.takeIf { it.unidadId == unidadId && it.idioma == idioma }

        this.unidadId = unidadId
        this.idioma = idioma
        this.perfilId = perfilId
        semilla = guardada?.semilla ?: System.currentTimeMillis()

        val preguntas = BancoDeContenido.generarPreguntas(unidad, idioma, semilla)
        _estado.value = LeccionUiState(
            tituloUnidad = unidad.titulo,
            preguntas = preguntas,
            indiceActual = (guardada?.indice ?: 0).coerceAtMost(preguntas.lastIndex).coerceAtLeast(0),
            aciertos = guardada?.aciertos ?: 0
        )
    }

    // El error no castiga: el primer fallo da una segunda oportunidad (criterio del especialista)
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

    // El punto de retoma se guarda al avanzar, cuando los aciertos ya corresponden a la pregunta
    fun siguientePregunta() {
        val actual = _estado.value
        val siguienteIndice = actual.indiceActual + 1

        if (siguienteIndice >= actual.preguntas.size) {
            repositorio.borrarLeccion(perfilId)
            _estado.update { it.copy(finalizado = true) }
        } else {
            repositorio.guardarLeccion(
                perfilId,
                LeccionGuardada(unidadId, idioma, semilla, siguienteIndice, actual.aciertos)
            )
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
