package com.example.turcoespanolkids.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Estado del permiso de ubicación, base para el futuro leaderboard por país
class UbicacionViewModel : ViewModel() {

    private val _concedida = MutableStateFlow(false)
    val concedida: StateFlow<Boolean> = _concedida

    var solicitudIniciada = false
        private set

    fun iniciarSolicitud() {
        solicitudIniciada = true
    }

    fun registrarResultado(concedida: Boolean) {
        solicitudIniciada = true
        _concedida.value = concedida
    }
}
