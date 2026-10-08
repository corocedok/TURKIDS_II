@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.turcoespanolkids.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turcoespanolkids.navigation.Screen
import com.example.turcoespanolkids.viewmodel.MainViewModel
import com.example.turcoespanolkids.viewmodel.UsuarioViewModel

/** Observa el MISMO UsuarioViewModel que RegistroScreen, sin pasar argumentos por la ruta. */
@Composable
fun ResumenRegistroScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val estado by usuarioViewModel.estado.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Resumen del registro") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Nombre: ${estado.nombre}")
                    Text("Correo: ${estado.correo}")
                    Text("Dirección: ${estado.direccion}")
                    Text("Contraseña: ${"*".repeat(estado.clave.length)}")
                    Text("Términos: ${if (estado.aceptaTerminos) "Aceptados" else "No aceptados"}")
                }
            }

            Button(
                onClick = {
                    usuarioViewModel.limpiar()
                    mainViewModel.navigateTo(
                        screen = Screen.Home,
                        popUpToRoute = Screen.Home,
                        inclusive = true
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Volver al inicio", fontSize = 18.sp)
            }
        }
    }
}