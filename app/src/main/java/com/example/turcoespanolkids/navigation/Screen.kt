package com.example.turcoespanolkids.navigation

/**
 * Rutas de navegación de la app, siguiendo el patrón sealed class
 * visto en Guía 10 para mayor seguridad de tipos.
 */
sealed class Screen(val route: String) {
    data object Home : Screen(route = "home")

    data class Leccion(val unidadId: String) : Screen(route = "leccion/$unidadId") {
        companion object {
            const val ROUTE_PATTERN = "leccion/{unidadId}"
            const val ARG_UNIDAD_ID = "unidadId"
        }
    }

    data class Resumen(val aciertos: Int, val total: Int) :
        Screen(route = "resumen/$aciertos/$total") {
        companion object {
            const val ROUTE_PATTERN = "resumen/{aciertos}/{total}"
            const val ARG_ACIERTOS = "aciertos"
            const val ARG_TOTAL = "total"
        }
    }
}
