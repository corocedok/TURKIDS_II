package com.example.turcoespanolkids.repository

import android.content.Context
import androidx.core.content.edit
import com.example.turcoespanolkids.model.PerfilesPrueba

// El avance se guarda solo en el dispositivo: id ficticio del perfil y estrellas por unidad
class ProgresoRepository(context: Context) {

    private val prefs = context.getSharedPreferences("progreso_turkids", Context.MODE_PRIVATE)

    fun perfilActivo(): String =
        prefs.getString(CLAVE_PERFIL, null) ?: PerfilesPrueba.lista.first().id

    fun guardarPerfilActivo(perfilId: String) {
        prefs.edit { putString(CLAVE_PERFIL, perfilId) }
    }

    fun estrellas(perfilId: String, unidadId: String): Int =
        prefs.getInt(clave(perfilId, unidadId), 0)

    // Se conserva el mejor resultado de cada unidad
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