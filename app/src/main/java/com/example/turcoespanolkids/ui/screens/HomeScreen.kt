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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turcoespanolkids.R
import com.example.turcoespanolkids.model.BancoDeContenido
import com.example.turcoespanolkids.model.Unidad
import com.example.turcoespanolkids.navigation.Screen
import com.example.turcoespanolkids.ui.utils.obtenerWindowSizeClass
import com.example.turcoespanolkids.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreenAdaptativo(mainViewModel: MainViewModel) {
    val windowSizeClass = obtenerWindowSizeClass()
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> HomeScreenCompacta(mainViewModel)
        WindowWidthSizeClass.Medium -> HomeScreenMediana(mainViewModel)
        WindowWidthSizeClass.Expanded -> HomeScreenExpandida(mainViewModel)
        else -> HomeScreenCompacta(mainViewModel)
    }
}

@Composable
fun HomeScreenCompacta(mainViewModel: MainViewModel) = HomeScreenBase(mainViewModel, columnas = 1)

@Composable
fun HomeScreenMediana(mainViewModel: MainViewModel) = HomeScreenBase(mainViewModel, columnas = 2)

@Composable
fun HomeScreenExpandida(mainViewModel: MainViewModel) = HomeScreenBase(mainViewModel, columnas = 3)

@Composable
private fun HomeScreenBase(mainViewModel: MainViewModel, columnas: Int) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menú",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()
                BancoDeContenido.unidades.forEach { unidad ->
                    NavigationDrawerItem(
                        label = { Text("${unidad.emoji}  ${unidad.titulo}") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            mainViewModel.navigateTo(Screen.Leccion(unidad.id))
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
                NavigationDrawerItem(
                    label = { Text("ℹ️  Acerca de") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        mainViewModel.navigateTo(Screen.AcercaDe)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Türkçe - Español", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
                var logoVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { logoVisible = true }
                AnimatedVisibility(
                    visible = logoVisible,
                    enter = fadeIn(tween(600)) + slideInVertically(
                        initialOffsetY = { -40 }, animationSpec = tween(600)
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo de la app",
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Elige una unidad para practicar",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columnas),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(BancoDeContenido.unidades) { index, unidad ->
                        TarjetaUnidad(
                            unidad = unidad,
                            index = index,
                            onClick = { mainViewModel.navigateTo(Screen.Leccion(unidad.id)) }
                        )
                    }
                }
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
        enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 60 }, animationSpec = tween(400))
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

@Preview(name = "Compact", widthDp = 360, heightDp = 800)
@Composable
private fun PreviewCompacta() {
    HomeScreenCompacta(mainViewModel = MainViewModel())
}

@Preview(name = "Medium", widthDp = 700, heightDp = 900)
@Composable
private fun PreviewMediana() {
    HomeScreenMediana(mainViewModel = MainViewModel())
}

@Preview(name = "Expanded", widthDp = 1200, heightDp = 900)
@Composable
private fun PreviewExpandida() {
    HomeScreenExpandida(mainViewModel = MainViewModel())
}