package com.example.turcoespanolkids.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.turcoespanolkids.model.BancoDeContenido
import com.example.turcoespanolkids.model.ProgresoUiState
import com.example.turcoespanolkids.model.calcularEstrellas
import com.example.turcoespanolkids.repository.ProgresoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProgresoViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ProgresoRepository(application)

    private val _estado = MutableStateFlow(cargar(repositorio.perfilActivo()))
    val estado: StateFlow<ProgresoUiState> = _estado

    private fun cargar(perfilId: String): ProgresoUiState {
        val estrellas = BancoDeContenido.unidades.associate { unidad ->
            unidad.id to repositorio.estrellas(perfilId, unidad.id)
        }
        return ProgresoUiState(perfilActivoId = perfilId, estrellas = estrellas)
    }

    fun seleccionarPerfil(perfilId: String) {
        repositorio.guardarPerfilActivo(perfilId)
        _estado.value = cargar(perfilId)
    }

    fun registrarResultado(unidadId: String, aciertos: Int, total: Int) {
        val perfilId = _estado.value.perfilActivoId
        repositorio.guardarEstrellas(perfilId, unidadId, calcularEstrellas(aciertos, total))
        _estado.value = cargar(perfilId)
    }

    fun reiniciar() {
        val perfilId = _estado.value.perfilActivoId
        repositorio.reiniciar(perfilId)
        _estado.value = cargar(perfilId)
    }
}