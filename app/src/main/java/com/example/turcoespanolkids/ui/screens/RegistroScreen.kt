@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.turcoespanolkids.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turcoespanolkids.navigation.Screen
import com.example.turcoespanolkids.viewmodel.MainViewModel
import com.example.turcoespanolkids.viewmodel.UsuarioViewModel

@Composable
fun RegistroScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val estado by usuarioViewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de prueba") },
                navigationIcon = {
                    IconButton(onClick = { mainViewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Usa solo datos ficticios",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            CampoTexto(
                valor = estado.nombre,
                etiqueta = "Nombre",
                error = estado.errores.nombre,
                onCambio = usuarioViewModel::onNombreChange
            )
            CampoTexto(
                valor = estado.correo,
                etiqueta = "Correo electrónico",
                error = estado.errores.correo,
                onCambio = usuarioViewModel::onCorreoChange,
                teclado = KeyboardType.Email
            )
            CampoTexto(
                valor = estado.clave,
                etiqueta = "Contraseña",
                error = estado.errores.clave,
                onCambio = usuarioViewModel::onClaveChange,
                esClave = true
            )
            CampoTexto(
                valor = estado.direccion,
                etiqueta = "Dirección",
                error = estado.errores.direccion,
                onCambio = usuarioViewModel::onDireccionChange
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = estado.aceptaTerminos,
                    onCheckedChange = usuarioViewModel::onAceptarTerminosChange
                )
                Text("Acepto los términos y condiciones")
            }
            estado.errores.terminos?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    if (usuarioViewModel.validarFormulario()) {
                        mainViewModel.navigateTo(Screen.ResumenRegistro)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Registrar", fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun CampoTexto(
    valor: String,
    etiqueta: String,
    error: String?,
    onCambio: (String) -> Unit,
    teclado: KeyboardType = KeyboardType.Text,
    esClave: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = {
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        visualTransformation = if (esClave) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (esClave) KeyboardType.Password else teclado),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}