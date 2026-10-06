@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.turcoespanolkids.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.turcoespanolkids.R
import com.example.turcoespanolkids.model.BancoDeContenido
import com.example.turcoespanolkids.model.Unidad
import com.example.turcoespanolkids.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Türkçe - Español", fontWeight = FontWeight.Bold) })
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
                var logoVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { logoVisible = true }
                AnimatedVisibility(
                    visible = logoVisible,
                    enter = fadeIn(tween(600)) + slideInVertically(
                        initialOffsetY = { -40 },
                        animationSpec = tween(600)
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo de la app",
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Elige una unidad para practicar",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            itemsIndexed(BancoDeContenido.unidades) { index, unidad ->
                TarjetaUnidad(
                    unidad = unidad,
                    index = index,
                    onClick = { navController.navigate(Screen.Leccion(unidad.id).route) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaUnidad(unidad: Unidad, index: Int, onClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(unidad.id) {
        delay(150L + index * 120L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + slideInVertically(
            initialOffsetY = { 60 },
            animationSpec = tween(400)
        )
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.94f else 1f,
            animationSpec = tween(120),
            label = "escalaTarjeta"
        )

        Surface(
            onClick = onClick,
            interactionSource = interactionSource,
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth().height(96.dp).scale(scale)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
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
}