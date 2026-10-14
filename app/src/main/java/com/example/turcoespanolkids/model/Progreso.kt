package com.example.turcoespanolkids.model


/** Perfil ficticio de prueba. No contiene datos personales reales. */
data class PerfilPrueba(val id: String, val nombre: String)

object PerfilesPrueba {
    val lista = listOf(
        PerfilPrueba(id = "perfil-prueba-1", nombre = "Prueba 1"),
        PerfilPrueba(id = "perfil-prueba-2", nombre = "Prueba 2")
    )
}

/** Estado del avance: perfil activo y estrellas (0 a 3) por unidad. */
data class ProgresoUiState(
    val perfilActivoId: String = PerfilesPrueba.lista.first().id,
    val estrellas: Map<String, Int> = emptyMap()
)

/** Misma regla de estrellas que usa ResumenScreen. */
fun calcularEstrellas(aciertos: Int, total: Int): Int = when {
    total == 0 -> 0
    aciertos == total -> 3
    aciertos >= total / 2 -> 2
    else -> 1
}