package com.example.turcoespanolkids.model

/**
 * Representa una palabra o frase bilingüe de ejemplo.
 * Contenido 100% ficticio/público, creado para el caso académico DSY1105.
 */
data class Palabra(
    val es: String,   // Palabra en español
    val tr: String,   // Palabra en turco
    val emoji: String // Recurso visual simple (evita necesidad de imágenes reales)
)

/**
 * Representa una unidad temática del módulo (ej. "Saludos", "Animales").
 */
data class Unidad(
    val id: String,
    val titulo: String,
    val emoji: String,
    val palabras: List<Palabra>
)

/**
 * Representa una pregunta de opción múltiple generada a partir de una Palabra.
 * @param mostrarTurco si es true, se pregunta "¿cómo se dice en español?" (turco -> español)
 */
data class Pregunta(
    val palabra: Palabra,
    val opciones: List<String>,
    val mostrarTurco: Boolean
) {
    val respuestaCorrecta: String
        get() = if (mostrarTurco) palabra.es else palabra.tr
}
