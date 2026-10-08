package com.example.turcoespanolkids.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.turcoespanolkids.model.UsuarioErrores
import com.example.turcoespanolkids.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel : ViewModel() {

    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor, errores = it.errores.copy(terminos = null)) }
    }

    /** Valida todos los campos, actualiza los errores y retorna true si todo es válido. */
    fun validarFormulario(): Boolean {
        val actual = _estado.value
        val errores = UsuarioErrores(
            nombre = if (actual.nombre.isBlank()) "Campo obligatorio" else null,
            correo = if (!Patterns.EMAIL_ADDRESS.matcher(actual.correo).matches()) "Correo inválido" else null,
            clave = if (actual.clave.length < 6) "Debe tener al menos 6 caracteres" else null,
            direccion = if (actual.direccion.isBlank()) "Campo obligatorio" else null,
            terminos = if (!actual.aceptaTerminos) "Debes aceptar los términos" else null
        )
        val hayErrores = listOfNotNull(
            errores.nombre, errores.correo, errores.clave, errores.direccion, errores.terminos
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }
        return !hayErrores
    }

    fun limpiar() {
        _estado.value = UsuarioUiState()
    }
}