package com.example.turkids.model

sealed interface UbicacionEstado {
    data object Verificando : UbicacionEstado
    data object NoEnUso : UbicacionEstado
    data class EnUso(val lugar: String) : UbicacionEstado
}

fun UbicacionEstado.texto(): String = when (this) {
    is UbicacionEstado.EnUso -> "Ubicación actual: $lugar"
    UbicacionEstado.Verificando -> "Obteniendo ubicación..."
    UbicacionEstado.NoEnUso -> "tu ubicacion no esta en uso"
}
