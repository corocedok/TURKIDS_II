# TurcoEspanolKids (Türkçe - Español)

Documento con la información actual del proyecto: contexto, arquitectura, pantallas, lógica, configuración, estado de Git y pendientes.

> **Estado de verificación:** el código fue revisado con el compilador de Kotlin (sin errores de sintaxis) y la lógica de preguntas se ejecutó por separado. El proyecto **no se compiló ni se ejecutó completo** en el entorno donde se armó, por lo que falta la prueba en Android Studio.

---

## 1. Resumen

App móvil nativa en Kotlin (Jetpack Compose) para el aprendizaje inicial de español dirigido a niños y niñas de habla turca menores de 10 años. Funciona como un juego tipo Duolingo: se muestra una palabra con su emoji y el usuario elige entre 3 alternativas.

| Dato | Valor |
|---|---|
| Nombre en pantalla | Türkçe - Español |
| `rootProject.name` | TurcoEspanolKids |
| Package / applicationId | `com.example.turcoespanolkids` |
| Asignatura | DSY1105 - Desarrollo de Aplicaciones Móviles |
| Trabajo | En parejas (Evaluación Parcial 2, encargo) |
| Contenido | Ficticio, de prueba, sin datos personales reales |

---

## 2. Contexto académico

**Evaluación Parcial 2**: interfaz funcional de una app móvil que resuelva una problemática real o simulada. Es una evaluación intermedia previa a la Evaluación Final Transversal (EFT).

- Asignación: semana 6. Entrega: semana 11 (antes de la primera sesión de Evaluación Parcial 3).
- Trabajo incompleto o fuera de plazo se califica con la nota mínima.
- Lo entregado sirve de base para el proyecto final.

### Requisitos y dónde se cubren

| Requisito | Dónde está en el proyecto |
|---|---|
| Diseño visual con Material 3 | Todas las pantallas (`Scaffold`, `TopAppBar`, `NavigationBar`, `ModalNavigationDrawer`, `FilterChip`, `Surface`, etc.) |
| Formularios validados | `RegistroScreen` + `UsuarioViewModel.validarFormulario()` |
| Navegación funcional | `NavHost` en `MainActivity`, eventos en `MainViewModel`, rutas en `Screen` |
| Gestión de estado | `StateFlow` + ViewModels (`LeccionViewModel`, `ProgresoViewModel`, `UsuarioViewModel`, `UbicacionViewModel`) |
| Almacenamiento local | `ProgresoRepository` con SharedPreferences |
| Uso de recursos nativos | Síntesis de voz (`TextToSpeech`) y permiso de ubicación |
| Animaciones | Transiciones de navegación, aparición de tarjetas y opciones, escala al presionar, confeti en el resumen |

---

## 3. Qué hace la app (flujo)

```
Practica ("¿Qué deseas practicar?")
   ├─ Turco   ┐
   └─ Español ┴──> Home
                     ├─ Unidad ──> Leccion ──> Resumen ──> Home
                     ├─ Menú lateral ──> Acerca de / Registro de prueba / Reiniciar progreso
                     └─ "Cambiar" ──> Practica
Registro ──> ResumenRegistro ──> Home
```

### Pantallas

| Pantalla | Archivo | Descripción |
|---|---|---|
| Practica | `PracticaScreen.kt` | Pantalla inicial. Dos tarjetas: **Turco** y **Español**. La elección define el idioma de todas las preguntas. |
| Home | `HomeScreen.kt` | Logo, perfil ficticio (Prueba 1 / Prueba 2), idioma actual con botón **Cambiar**, y tarjetas de unidades con estrellas. Se adapta al ancho de pantalla: 1, 2 o 3 columnas. Menú lateral con las unidades, Acerca de, Registro de prueba y Reiniciar progreso. |
| Leccion | `LeccionScreen.kt` | Barra de progreso, emoji de la palabra, botón **Escuchar**, pregunta, 3 opciones que aparecen de forma escalonada, mensajes de ánimo y botón **Continuar**. |
| Resumen | `ResumenScreen.kt` | Estrellas obtenidas, aciertos sobre el total y confeti animado. Botón **Volver al inicio**. |
| Registro | `RegistroScreen.kt` | Formulario de prueba con validaciones. |
| ResumenRegistro | `ResumenRegistroScreen.kt` | Muestra los datos ingresados (la contraseña va oculta) y limpia el formulario al volver. |
| Acerca de | `AcercaDeScreen.kt` | Descripción del MVP, con `NavigationBar` (Inicio / Acerca de). |

---

## 4. Mecánica del juego

### Preguntas y alternativas
- `BancoDeContenido.generarPreguntas(unidad, idioma, semilla)` crea una pregunta por palabra de la unidad.
- Cada pregunta tiene **3 opciones**: la correcta y 2 distractores de la **misma unidad**, sin repetir.
- **Idioma de práctica:**
  - **Turco:** se muestra la palabra en turco y se responde en español.
  - **Español:** se muestra la palabra en español y se responde en turco.
  - Las preguntas **nunca se mezclan** entre idiomas.
- La **semilla** hace que la misma lección se pueda reproducir idéntica (necesario para retomar una lección guardada).

### Validación de respuesta (`LeccionViewModel`)
- Respuesta correcta: se marca, suma un acierto y aparece **Continuar**.
- Primer error: la opción se descarta (naranja) y se da una **segunda oportunidad** (criterio del especialista: el error no castiga).
- Segundo error: se muestra la respuesta correcta y se avanza sin sumar.

### Estrellas (`calcularEstrellas`)
| Resultado | Estrellas |
|---|---|
| Todas correctas | 3 |
| Mitad o más | 2 |
| Menos de la mitad | 1 |
| Sin preguntas | 0 |

Se conserva el **mejor resultado** de cada unidad por perfil.

### Audio
- `TextToSpeech` pronuncia la palabra que se ve en pantalla: turco (`tr-TR`) o español (`es-US`).
- Se reproduce automáticamente al aparecer la pregunta (con un retraso de 800 ms) y con el botón **Escuchar**.
- Si falta la voz del idioma en el dispositivo, el botón muestra un aviso. El audio se detiene al salir de la app (`ON_STOP`).

---

## 5. Contenido (`BancoDeContenido`)

Contenido ficticio de prueba. Las unidades y su orden se ajustan según el especialista.

| Unidad (`id`) | Palabras (es - tr) |
|---|---|
| Saludos (`saludos`) | Hola - Merhaba · Gracias - Teşekkürler · Sí - Evet · No - Hayır |
| Animales (`animales`) | Gato - Kedi · Perro - Köpek · Pájaro - Kuş · Pez - Balık |
| Números (`numeros`) | Uno - Bir · Dos - İki · Tres - Üç · Cuatro - Dört |

Cada palabra lleva un emoji como recurso visual (no usa imágenes reales).

---

## 6. Persistencia local

SharedPreferences `progreso_turkids`, gestionado por `ProgresoRepository`.

| Qué se guarda | Clave |
|---|---|
| Perfil activo | `perfil_activo` |
| Idioma de práctica | `idioma_practica` |
| Estrellas por perfil y unidad | `estrellas_{perfilId}_{unidadId}` |
| Lección en curso (retoma) | `leccion_{perfilId}_unidad`, `_idioma`, `_semilla`, `_indice`, `_aciertos` |

- **Retoma de lección:** se guarda al pulsar **Continuar**, y se borra al terminar la lección. Si se vuelve a entrar a la misma unidad con el mismo idioma, continúa desde esa pregunta con sus aciertos. Hay un solo punto de retoma por perfil.
- **Reiniciar progreso** (menú lateral) borra las estrellas y la lección en curso del perfil activo.
- **Lo que no se guarda a propósito:** los datos del formulario de registro (son datos personales y la app está pensada para menores).
- No se usan Room ni DataStore.

---

## 7. Permisos y recursos nativos

### Ubicación
- Manifest: `ACCESS_COARSE_LOCATION` y `ACCESS_FINE_LOCATION`, más `uses-feature` de ubicación y GPS con `required="false"`.
- Al iniciar la app (`MainActivity`) se muestra el **diálogo nativo** del sistema. Si ya estaba concedido, no se vuelve a preguntar.
- Si el usuario acepta o rechaza, la app continúa sin problemas. Si rechaza de forma permanente, el sistema responde sin mostrar el diálogo.
- El resultado queda en `UbicacionViewModel` (`concedida`). Es una **preparación para el futuro leaderboard por país**: todavía no se lee la ubicación.

### Voz
- `TextToSpeech` con declaración `<queries>` para el servicio TTS (necesaria desde Android 11).

---

## 8. Arquitectura

Patrón **MVVM** con una sola actividad y navegación por eventos.

```
app/src/main/java/com/example/turcoespanolkids/
├── MainActivity.kt
├── model/
│   ├── BancoDeContenido.kt     unidades, palabras y generación de preguntas
│   ├── IdiomaPractica.kt       TURCO / ESPANOL
│   ├── LeccionUiState.kt       estado de una lección
│   ├── Palabra.kt              Palabra, Unidad, Pregunta
│   ├── Progreso.kt             perfiles de prueba, ProgresoUiState, LeccionGuardada, calcularEstrellas
│   └── UsuarioUiState.kt       estado y errores del formulario
├── navigation/
│   ├── Screen.kt               rutas (sealed class)
│   └── NavigationEvent.kt      NavigateTo, PopBackStack, NavigateUp
├── repository/
│   └── ProgresoRepository.kt   SharedPreferences
├── viewmodel/
│   ├── MainViewModel.kt        emite eventos de navegación
│   ├── LeccionViewModel.kt     lógica de la lección
│   ├── ProgresoViewModel.kt    perfil, idioma y estrellas
│   ├── UsuarioViewModel.kt     validación del registro
│   └── UbicacionViewModel.kt   estado del permiso de ubicación
└── ui/
    ├── screens/                7 pantallas
    ├── theme/                  Color, Theme (colores dinámicos en Android 12+), Type
    └── utils/WindowSizeUtils.kt   obtenerWindowSizeClass()
```

### Navegación
- Las pantallas no usan el `NavController` directamente: llaman a `MainViewModel.navigateTo(...)`, que emite un `NavigationEvent` por un `SharedFlow`.
- `MainActivity` recoge los eventos (`collectLatest`) y los ejecuta sobre el `NavController`, con soporte de `popUpTo` (inclusive) y `launchSingleTop`.

| Ruta | Valor |
|---|---|
| Practica (inicio) | `practica` |
| Home | `home` |
| Acerca de | `acerca_de` |
| Registro | `registro` |
| ResumenRegistro | `resumen_registro` |
| Leccion | `leccion/{unidadId}` |
| Resumen | `resumen/{aciertos}/{total}` |

### Animaciones
- Transiciones entre pantallas: deslizamiento horizontal con fundido para la lección, vertical para el resumen, y fundidos para el resto.
- Aparición escalonada de tarjetas de unidad y de las opciones de respuesta.
- Escala al presionar tarjetas y botones, y cambio animado de color en las opciones.
- Confeti dibujado en `Canvas` en el resumen (40 partículas, 2,2 s).

---

## 9. Validaciones del formulario de registro

| Campo | Regla | Mensaje |
|---|---|---|
| Nombre | No vacío | Campo obligatorio |
| Correo | Formato de correo (`Patterns.EMAIL_ADDRESS`) | Correo inválido |
| Contraseña | Mínimo 6 caracteres | Debe tener al menos 6 caracteres |
| Dirección | No vacía | Campo obligatorio |
| Términos | Debe estar marcado | Debes aceptar los términos |

El error de cada campo se borra al volver a escribir en él.

---

## 10. Configuración de compilación

| Elemento | Versión |
|---|---|
| Android Gradle Plugin | 9.2.1 |
| Gradle (wrapper) | 9.4.1 |
| Kotlin | 2.2.10 (plugin `kotlin.compose`; sin `kotlin.android`, AGP 9 lo trae integrado) |
| compileSdk / targetSdk / minSdk | 37 / 36 / 24 |
| Java | 11 |
| Compose BOM | 2026.02.01 |
| Material 3 window size class | 1.4.0 |
| Navigation Compose | 2.9.8 |
| Lifecycle (runtime y viewmodel-compose) | 2.10.0 |
| Activity Compose | 1.13.0 |
| Core KTX | 1.19.0 |
| Coroutines | 1.10.2 |
| Pruebas | JUnit 4.13.2, AndroidX JUnit 1.3.0, Espresso 3.7.0 |

- Todas las dependencias se declaran en `gradle/libs.versions.toml` (catálogo de versiones).
- `app/build.gradle.kts` usa `material-icons-core` para los íconos (Menu, Home, Info, ArrowBack).
- `local.properties` no se versiona: cada PC genera el suyo con la ruta del SDK.

---

## 11. Manifest y recursos

- Una sola actividad, `MainActivity`, con `MAIN` + `LAUNCHER`, `exported="true"`, `launchMode="singleTop"` y `windowSoftInputMode="adjustResize"`.
- Ícono de la app: logo TURKIDS (gato con fez). Ícono adaptable (fondo `#F7F8F1` y primer plano en `drawable-nodpi/ic_launcher_foreground.png`), más versiones raster `ic_launcher` e `ic_launcher_round` para API 24 y 25. No tiene ícono monocromático.
- `drawable/logo.png`: el logo que se muestra dentro de la app (Practica y Home).
- Tema: `Theme.TurcoEspanolKids` (padre `android:Theme.Material.Light.NoActionBar`) y `enableEdgeToEdge()`.

---

## 12. Cómo se llegó a este proyecto

El proyecto es la **unificación** de dos repositorios del equipo:

| Origen | Qué aportó |
|---|---|
| **TURKIDS_II** (repo de la compañera) | Toda la app funcional: unidades, preguntas, validación, estrellas, audio, registro, animaciones, SharedPreferences. Es la base del código. |
| **AppTraductor_Grupo** (repo propio) | Configuración de Gradle y catálogo de versiones. No tenía lógica de preguntas ni de turco; sus pantallas de registro e inicio estaban duplicadas por las de TURKIDS. |

**Limpieza y cambios al unificar**
- Se eliminaron los comentarios explicativos y de plantilla en `.kt` y `.xml`; quedaron solo comentarios cortos de reglas del negocio.
- Las tres versiones de la pantalla de inicio (compacta, mediana y expandida) se fusionaron en una que elige 1, 2 o 3 columnas.
- `ResumenScreen` reutiliza `calcularEstrellas`, para tener la regla de estrellas en un solo lugar.
- `utils.kt` pasó a `ui/utils/WindowSizeUtils.kt`.
- Se usaron las versiones de AGP y Gradle de AppTraductor (más antiguas que las de TURKIDS: 9.4.1 y 9.6.0), con `targetSdk` 36 en vez de 37.

**Mejoras posteriores**
1. Pantalla inicial para elegir idioma de práctica (filtra las preguntas).
2. Solicitud del permiso de ubicación al iniciar.
3. Retoma de la lección en curso e idioma persistidos.
4. Ajustes del manifest, ícono propio y detención del audio al salir de la app.

---

## 13. Repositorio y Git

| Dato | Valor |
|---|---|
| Repositorio de trabajo | `https://github.com/corocedok/TURKIDS_II.git` (de la compañera) |
| Rama principal | `master` |
| Rama del trabajo unificado | `develop` (separada, sin mezclar a `master`) |
| Otras ramas | `feature/progreso-perfil` (PR #3), `feature/home-screen`, `feature/registro-formulario` |
| Repo propio anterior | `Safari-ux/AppTraductor_Grupo` (rama `main`) |

- La rama `develop` se creó a partir de `master` y se subió con `git push -u origin develop`.
- Decisión: mantenerla **separada** hasta probarla. No usar **Merge pull request**.
- `feature/home-screen` tenía 1 commit que `master` no tenía y la unificación no lo incluye. Conviene revisarlo.
- Los cambios de la última iteración (idioma, ubicación, retoma, ícono) pueden no estar subidos aún: revisar con `git status`.
- En el repo de la compañera hay archivos de `.idea/` y `.kotlin/` versionados. Normalmente no se suben, y conviene hablarlo con ella.

### Flujo para subir cambios (Git Bash, dentro de la carpeta del repo)
```bash
git status
git add .
git commit -m "descripción del cambio"
git push
```
Si el `push` da error 403, falta permiso de escritura en ese repo (agregar como colaborador en Settings → Collaborators).

---

## 14. Pendientes y riesgos

### Pruebas por hacer
- [ ] Sync de Gradle y compilación sin errores.
- [ ] Jugar cada unidad completa en **Turco** y en **Español** (comprobar que no se mezclan).
- [ ] Probar la segunda oportunidad al fallar y el resumen con confeti.
- [ ] Audio: escuchar en turco y en español (puede faltar la voz instalada en el emulador).
- [ ] Cerrar la app a mitad de una lección, volver a abrirla y retomar.
- [ ] Cerrar y abrir con estrellas ya guardadas.
- [ ] Diálogo de ubicación: aceptar (preciso y aproximado) y rechazar.
- [ ] Validaciones del registro.
- [ ] Vista en tablet (1, 2 y 3 columnas) y en horizontal.

### Puntos abiertos
- **Ícono en el cajón de aplicaciones:** se reportó que no se veía. Pendiente de confirmar si aparece al deslizar hacia arriba en el emulador y, si no, revisar Logcat (`FATAL EXCEPTION`) y la pestaña Run. Se recomienda desinstalar la app antes de volver a ejecutar para refrescar el ícono.
- **Apertura automática al ejecutar:** es el comportamiento normal de Android Studio. Se desactiva en Run → Edit Configurations → Launch Options → Launch: Nothing.
- Revisar con la compañera el commit de `feature/home-screen`.
- Decidir con ella cómo y cuándo llevar `develop` a `master`.

### Limitaciones conocidas
- **No hay login:** el progreso es por perfil ficticio (Prueba 1 / Prueba 2) y vive solo en el dispositivo.
- Si se desinstala la app o se borran sus datos, se pierde el progreso.
- La ubicación solo se solicita; todavía no se usa.
- Un solo punto de retoma por perfil: empezar otra unidad reemplaza el anterior.
- El contenido es mínimo (3 unidades de 4 palabras).

### Ideas futuras
- Leaderboard por país usando la ubicación (requiere un backend).
- Más unidades y contenido definido con el especialista.
- Cuentas de usuario y respaldo del progreso.
- Ícono monocromático para los íconos temáticos de Android 13+.
