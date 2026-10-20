package com.example.turcoespanolkids.repository

import android.content.Context
import androidx.core.content.edit
import com.example.turcoespanolkids.model.IdiomaPractica
import com.example.turcoespanolkids.model.LeccionGuardada
import com.example.turcoespanolkids.model.PerfilesPrueba

// El avance se guarda solo en el dispositivo: perfil ficticio, idioma, estrellas por unidad y lección en curso
class ProgresoRepository(context: Context) {

    private val prefs = context.getSharedPreferences("progreso_turkids", Context.MODE_PRIVATE)

    fun perfilActivo(): String =
        prefs.getString(CLAVE_PERFIL, null) ?: PerfilesPrueba.lista.first().id

    fun guardarPerfilActivo(perfilId: String) {
        prefs.edit { putString(CLAVE_PERFIL, perfilId) }
    }

    fun idiomaPractica(): IdiomaPractica =
        idiomaDesde(prefs.getString(CLAVE_IDIOMA, null)) ?: IdiomaPractica.TURCO

    fun guardarIdiomaPractica(idioma: IdiomaPractica) {
        prefs.edit { putString(CLAVE_IDIOMA, idioma.name) }
    }

    fun estrellas(perfilId: String, unidadId: String): Int =
        prefs.getInt(clave(perfilId, unidadId), 0)

    // Se conserva el mejor resultado de cada unidad
    fun guardarEstrellas(perfilId: String, unidadId: String, estrellas: Int) {
        val mejor = maxOf(estrellas(perfilId, unidadId), estrellas)
        prefs.edit { putInt(clave(perfilId, unidadId), mejor) }
    }

    fun leccionGuardada(perfilId: String): LeccionGuardada? {
        val prefijo = prefijoLeccion(perfilId)
        val unidadId = prefs.getString("${prefijo}unidad", null) ?: return null
        val idioma = idiomaDesde(prefs.getString("${prefijo}idioma", null)) ?: return null
        return LeccionGuardada(
            unidadId = unidadId,
            idioma = idioma,
            semilla = prefs.getLong("${prefijo}semilla", 0L),
            indice = prefs.getInt("${prefijo}indice", 0),
            aciertos = prefs.getInt("${prefijo}aciertos", 0)
        )
    }

    fun guardarLeccion(perfilId: String, leccion: LeccionGuardada) {
        val prefijo = prefijoLeccion(perfilId)
        prefs.edit {
            putString("${prefijo}unidad", leccion.unidadId)
            putString("${prefijo}idioma", leccion.idioma.name)
            putLong("${prefijo}semilla", leccion.semilla)
            putInt("${prefijo}indice", leccion.indice)
            putInt("${prefijo}aciertos", leccion.aciertos)
        }
    }

    fun borrarLeccion(perfilId: String) {
        val prefijo = prefijoLeccion(perfilId)
        prefs.edit {
            prefs.all.keys.filter { it.startsWith(prefijo) }.forEach { remove(it) }
        }
    }

    fun reiniciar(perfilId: String) {
        val prefijos = listOf("estrellas_${perfilId}_", prefijoLeccion(perfilId))
        prefs.edit {
            prefs.all.keys
                .filter { clave -> prefijos.any { clave.startsWith(it) } }
                .forEach { remove(it) }
        }
    }

    private fun idiomaDesde(nombre: String?): IdiomaPractica? =
        IdiomaPractica.entries.firstOrNull { it.name == nombre }

    private fun clave(perfilId: String, unidadId: String) = "estrellas_${perfilId}_$unidadId"

    private fun prefijoLeccion(perfilId: String) = "leccion_${perfilId}_"

    private companion object {
        const val CLAVE_PERFIL = "perfil_activo"
        const val CLAVE_IDIOMA = "idioma_practica"
    }
}
