package com.example.turcoespanolkids.repository

import android.content.Context
import androidx.core.content.edit
import com.example.turcoespanolkids.model.PerfilesPrueba

/**
 * Guarda el avance solo en el dispositivo.
 * No se guardan datos personales: solo el id ficticio del perfil y las estrellas por unidad.
 */
class ProgresoRepository(context: Context) {

    private val prefs = context.getSharedPreferences("progreso_turkids", Context.MODE_PRIVATE)

    fun perfilActivo(): String =
        prefs.getString(CLAVE_PERFIL, null) ?: PerfilesPrueba.lista.first().id

    fun guardarPerfilActivo(perfilId: String) {
        prefs.edit { putString(CLAVE_PERFIL, perfilId) }
    }

    fun estrellas(perfilId: String, unidadId: String): Int =
        prefs.getInt(clave(perfilId, unidadId), 0)

    /** Conserva el mejor resultado obtenido en la unidad. */
    fun guardarEstrellas(perfilId: String, unidadId: String, estrellas: Int) {
        val mejor = maxOf(estrellas(perfilId, unidadId), estrellas)
        prefs.edit { putInt(clave(perfilId, unidadId), mejor) }
    }

    fun reiniciar(perfilId: String) {
        val prefijo = "estrellas_${perfilId}_"
        prefs.edit {
            prefs.all.keys.filter { it.startsWith(prefijo) }.forEach { remove(it) }
        }
    }

    private fun clave(perfilId: String, unidadId: String) = "estrellas_${perfilId}_$unidadId"

    private companion object {
        const val CLAVE_PERFIL = "perfil_activo"
    }
}