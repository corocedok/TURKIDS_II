@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.turcoespanolkids.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.turcoespanolkids.model.BancoDeContenido
import com.example.turcoespanolkids.model.Unidad
import com.example.turcoespanolkids.navigation.Screen

/**
 * Pantalla principal: muestra las unidades disponibles como tarjetas grandes,
 * al estilo "camino de aprendizaje" de Duolingo, pero simplificado para
 * mantener la navegación clara y acotada para menores de 10 años.
 */
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Türkçe - Español", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Elige una unidad para practicar",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(BancoDeContenido.unidades) { unidad ->
                TarjetaUnidad(
                    unidad = unidad,
                    onClick = {
                        navController.navigate(Screen.Leccion(unidad.id).route)
                    }
                )
            }
        }
    }
}

@Composable
private fun TarjetaUnidad(unidad: Unidad, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = unidad.emoji, fontSize = 40.sp)
            Text(
                text = unidad.titulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
