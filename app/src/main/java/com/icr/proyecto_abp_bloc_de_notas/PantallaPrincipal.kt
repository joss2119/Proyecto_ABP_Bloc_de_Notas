
package com.icr.proyecto_abp_bloc_de_notas

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.icr.proyecto_abp_bloc_de_notas.ui.theme.Proyecto_ABP_Bloc_de_NotasTheme
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.material.icons.filled.ArrowBack

// Definición del Data Class para Nota
data class Nota(
    var id: Int,
    var titulo: String,
    var contenido: String,
    var estaEnPapelera: Boolean = false
)

// Enum para gestionar la vista actual (Notas Activas o Papelera)
enum class VistaActual {
    NOTAS_ACTIVAS,
    PAPELERA
}

// Enum para la navegación entre pantallas principales
enum class AppScreen {
    NOTES_LIST,
    SETTINGS,
    ABOUT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipalNotasApp(
    initialDarkTheme: Boolean = false, // Se recibe desde MainActivity
    onThemeChange: (Boolean) -> Unit  // Se recibe desde MainActivity
) {
    var currentScreen by remember { mutableStateOf(AppScreen.NOTES_LIST) }
    // El estado del tema se maneja aquí para que persista entre pantallas
    var isDarkTheme by remember { mutableStateOf(initialDarkTheme) }

    Proyecto_ABP_Bloc_de_NotasTheme(darkTheme = isDarkTheme) {
        when (currentScreen) {
            AppScreen.NOTES_LIST -> {
                PantallaPrincipalNotas(
                    onNavigateToSettings = { currentScreen = AppScreen.SETTINGS },
                    onNavigateToAbout = { currentScreen = AppScreen.ABOUT }
                    // Pasamos isDarkTheme para que PantallaPrincipalNotas pueda usarlo si es necesario,
                    // aunque el tema ya se aplica en Proyecto_ABP_Bloc_de_NotasTheme
                )
            }
            AppScreen.SETTINGS -> {
                PantallaAjustes( // Asume que tienes este Composable definido
                    isDarkTheme = isDarkTheme,
                    onThemeChanged = { newDarkThemeState ->
                        isDarkTheme = newDarkThemeState
                        onThemeChange(newDarkThemeState) // Notifica a MainActivity para guardar
                    },
                    onNavigateBack = { currentScreen = AppScreen.NOTES_LIST }
                )
            }
            AppScreen.ABOUT -> {
                PantallaAcercaDe( // Asume que tienes este Composable definido
                    onNavigateBack = { currentScreen = AppScreen.NOTES_LIST }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PantallaPrincipalNotas(
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val estadoCajonNavegacion = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Esta lista debe ser la fuente de verdad para todas las notas
    // Para persistencia real, esto vendría de un ViewModel y un Repositorio/Base de Datos
    val listaDeNotasGlobal = remember { mutableStateListOf<Nota>() }
    var contadorDeNotas by remember { mutableStateOf(0) } // Simple ID generator

    // Estados para el diálogo de edición de notas
    var mostrarDialogoEdicion by remember { mutableStateOf(false) }
    var notaParaEditar by remember { mutableStateOf<Nota?>(null) }
    var tituloEditado by remember { mutableStateOf("") }
    var contenidoEditado by remember { mutableStateOf("") }

    // Estado para el Navigation Drawer
    var selectedItemIndexNavDrawer by remember { mutableStateOf(0) }

    // Estados para diálogos de acciones sobre notas
    var notaParaAccionLarga by remember { mutableStateOf<Nota?>(null) }
    var mostrarDialogoEnviarAPapeleraConfirmacion by remember { mutableStateOf(false) }
    var mostrarDialogoVaciarPapeleraConfirmacion by remember { mutableStateOf(false) }
    // Podrías añadir más para restaurar/eliminar individualmente desde la papelera

    // Estado para controlar qué vista mostrar (Notas Activas o Papelera)
    var vistaActual by remember { mutableStateOf(VistaActual.NOTAS_ACTIVAS) }

    ModalNavigationDrawer(
        drawerState = estadoCajonNavegacion,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.titulo_cajon),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.notas_activas)) },
                    label = { Text(stringResource(R.string.notas_activas)) },
                    selected = selectedItemIndexNavDrawer == 0,
                    onClick = {
                        vistaActual = VistaActual.NOTAS_ACTIVAS
                        selectedItemIndexNavDrawer = 0
                        scope.launch { estadoCajonNavegacion.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.papelera)) },
                    label = { Text(stringResource(R.string.papelera)) },
                    selected = selectedItemIndexNavDrawer == 1,
                    onClick = {
                        vistaActual = VistaActual.PAPELERA
                        selectedItemIndexNavDrawer = 1
                        scope.launch { estadoCajonNavegacion.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.acerca_de)) },
                    label = { Text(stringResource(R.string.acerca_de)) },
                    selected = selectedItemIndexNavDrawer == 2,
                    onClick = {
                        selectedItemIndexNavDrawer = 2
                        scope.launch { estadoCajonNavegacion.close() }
                        onNavigateToAbout() // Navega a la pantalla "Acerca de"
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(Modifier.weight(1f)) // Empuja lo siguiente hacia abajo
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_name)) },
                    label = { Text(stringResource(R.string.settings_name)) },
                    selected = selectedItemIndexNavDrawer == 3, // Asume que el índice 3 es para Ajustes
                    onClick = {
                        selectedItemIndexNavDrawer = 3
                        scope.launch { estadoCajonNavegacion.close() }
                        onNavigateToSettings() // Navega a la pantalla de Ajustes
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (vistaActual == VistaActual.NOTAS_ACTIVAS) stringResource(R.string.nombre_aplicacion)
                            else stringResource(R.string.papelera),
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                estadoCajonNavegacion.apply { if (isClosed) open() else close() }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(R.string.menu_descripcion)
                            )
                        }
                    },
                    actions = {
                        // Botón de Ajustes en la TopAppBar que navega a la pantalla de Ajustes
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.settings_name)
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                when (vistaActual) {
                    VistaActual.NOTAS_ACTIVAS -> {
                        FloatingActionButton(onClick = {
                            contadorDeNotas++
                            val nuevaNota = Nota(
                                id = contadorDeNotas, // Usar el contador como ID (simple)
                                titulo = "${context.getString(R.string.nota_nueva_titulo_prefijo)} $contadorDeNotas",
                                contenido = ""
                            )
                            // Pre-rellenar para el diálogo de edición
                            notaParaEditar = nuevaNota // Marcar como nueva nota para editar
                            tituloEditado = nuevaNota.titulo
                            contenidoEditado = nuevaNota.contenido
                            mostrarDialogoEdicion = true
                        }) {
                            Icon(Icons.Filled.Add, stringResource(R.string.anadir_nueva_nota))
                        }
                    }
                    VistaActual.PAPELERA -> {
                        if (listaDeNotasGlobal.any { it.estaEnPapelera }) {
                            FloatingActionButton(
                                onClick = {
                                    mostrarDialogoVaciarPapeleraConfirmacion = true
                                },
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ) {
                                Icon(
                                    // Icons.Filled.DeleteForever, // Comentado o eliminado
                                    imageVector = Icons.Filled.Delete, // Opción 1: Usar Delete normal
                                    // imageVector = Icons.Filled.DeleteOutline, // Opción 2: Usar DeleteOutline
                                    contentDescription = stringResource(R.string.vaciar_papelera_descripcion)
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { innerPadding ->
            val notasAMostrar = when (vistaActual) {
                VistaActual.NOTAS_ACTIVAS -> listaDeNotasGlobal.filter { !it.estaEnPapelera }
                VistaActual.PAPELERA -> listaDeNotasGlobal.filter { it.estaEnPapelera }
            }

            if (notasAMostrar.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (vistaActual == VistaActual.NOTAS_ACTIVAS) stringResource(R.string.mensaje_no_hay_notas)
                        else stringResource(R.string.mensaje_papelera_vacia)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(notasAMostrar, key = { it.id }) { nota ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .pointerInput(nota) { // Usar 'nota' como key aquí
                                    detectTapGestures(
                                        onTap = {
                                            if (vistaActual == VistaActual.NOTAS_ACTIVAS && !nota.estaEnPapelera) {
                                                notaParaEditar = nota
                                                tituloEditado = nota.titulo
                                                contenidoEditado = nota.contenido
                                                mostrarDialogoEdicion = true
                                            } else if (vistaActual == VistaActual.PAPELERA && nota.estaEnPapelera) {
                                                // TODO: Implementar opciones para notas en papelera (Restaurar, Eliminar permanente individual)
                                                // Podrías mostrar un BottomSheet o un AlertDialog con opciones
                                                Toast.makeText(
                                                    context,
                                                    "${context.getString(R.string.opciones_para_nota_en_papelera)}: ${nota.titulo}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        onLongPress = {
                                            if (vistaActual == VistaActual.NOTAS_ACTIVAS && !nota.estaEnPapelera) {
                                                notaParaAccionLarga = nota
                                                mostrarDialogoEnviarAPapeleraConfirmacion = true
                                            }
                                            // Si está en papelera, podrías ofrecer un menú contextual diferente con onLongPress
                                        }
                                    )
                                }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(nota.titulo, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    nota.contenido,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 3 // Limita las líneas en la vista de lista
                                )
                                if (nota.estaEnPapelera) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        stringResource(R.string.en_papelera_label),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de Edición de Nota
    if (mostrarDialogoEdicion) { // No es necesario notaParaEditar != null si la lógica de apertura es correcta
        val esNuevaNota = listaDeNotasGlobal.find { it.id == notaParaEditar?.id } == null

        AlertDialog(
            onDismissRequest = {
                // Si es una nueva nota y se cancela, no la añadimos si no se guardó
                // (aunque en este flujo ya se añade al hacer clic en FAB y luego se edita)
                mostrarDialogoEdicion = false
                notaParaEditar = null // Limpiar siempre
            },
            title = { Text(if (esNuevaNota || notaParaEditar?.titulo?.startsWith(stringResource(R.string.nota_nueva_titulo_prefijo)) == true) stringResource(R.string.crear_nota_titulo) else stringResource(R.string.editar_nota_titulo)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = tituloEditado,
                        onValueChange = { tituloEditado = it },
                        label = { Text(stringResource(R.string.titulo_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = contenidoEditado,
                        onValueChange = { contenidoEditado = it },
                        label = { Text(stringResource(R.string.contenido_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp) // Altura para el contenido
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        notaParaEditar?.let { notaActual ->
                            val index = listaDeNotasGlobal.indexOfFirst { it.id == notaActual.id }
                            if (index != -1) { // Editando nota existente
                                listaDeNotasGlobal[index] = notaActual.copy(
                                    titulo = tituloEditado.ifBlank { context.getString(R.string.nota_sin_titulo) },
                                    contenido = contenidoEditado
                                )
                            } else { // Creando nueva nota (ya se añadió al hacer clic en FAB)
                                // Actualizar la nota que se añadió con el ID temporal si es necesario
                                // O si la lógica es que solo se añade aquí tras confirmar:
                                val notaAGuardar = notaActual.copy(
                                    titulo = tituloEditado.ifBlank { context.getString(R.string.nota_sin_titulo) },
                                    contenido = contenidoEditado
                                )
                                // Si es realmente una nota nueva que no está en la lista global aún
                                if (listaDeNotasGlobal.none{it.id == notaAGuardar.id}) {
                                    listaDeNotasGlobal.add(notaAGuardar)
                                } else { // Es una nota que ya estaba (el caso del FAB que la añade y luego edita)
                                    val existingIndex = listaDeNotasGlobal.indexOfFirst { it.id == notaAGuardar.id }
                                    if(existingIndex != -1) listaDeNotasGlobal[existingIndex] = notaAGuardar else TODO()
                                }
                            }
                        }
                        mostrarDialogoEdicion = false
                        notaParaEditar = null
                    }
                ) { Text(stringResource(R.string.guardar_boton)) }
            },
            dismissButton = {
                Button(onClick = {
                    // Si es una nota nueva y no se guardó, se podría eliminar de listaDeNotasGlobal aquí
                    // Pero la lógica actual la añade al pulsar el FAB.
                    // Si no quieres que se añada hasta guardar, el FAB no debería añadirla a la lista.
                    mostrarDialogoEdicion = false
                    notaParaEditar = null
                }) { Text(stringResource(R.string.cancelar_boton)) }
            }
        )
    }

    // Diálogo de confirmación para ENVIAR a la papelera
    if (mostrarDialogoEnviarAPapeleraConfirmacion && notaParaAccionLarga != null) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoEnviarAPapeleraConfirmacion = false
                notaParaAccionLarga = null
            },
            icon = { Icon(Icons.Filled.DeleteSweep, contentDescription = null) },
            title = { Text(stringResource(R.string.enviar_a_papelera_titulo)) },
            text = { Text(stringResource(R.string.confirmar_enviar_a_papelera_mensaje, notaParaAccionLarga?.titulo ?: "")) },
            confirmButton = {
                Button(
                    onClick = {
                        notaParaAccionLarga?.let { notaAMover ->
                            val index = listaDeNotasGlobal.indexOfFirst { it.id == notaAMover.id }
                            if (index != -1) {
                                listaDeNotasGlobal[index] = notaAMover.copy(estaEnPapelera = true)
                                // USA context.getString() AQUÍ
                                Toast.makeText(context, context.getString(R.string.nota_enviada_a_papelera_toast), Toast.LENGTH_SHORT).show()
                            }
                        }
                        mostrarDialogoEnviarAPapeleraConfirmacion = false
                        notaParaAccionLarga = null
                    }
                ) { Text(stringResource(R.string.enviar_boton)) } // stringResource está bien aquí
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoEnviarAPapeleraConfirmacion = false
                    notaParaAccionLarga = null
                }) { Text(stringResource(R.string.cancelar_boton)) } // stringResource está bien aquí
            }
        )
    }

    // Diálogo de confirmación para VACIAR PAPELERA
    if (mostrarDialogoVaciarPapeleraConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoVaciarPapeleraConfirmacion = false },
            icon = { Icon(Icons.Filled.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text(stringResource(R.string.vaciar_papelera_confirmacion_titulo)) },
            text = { Text(stringResource(R.string.vaciar_papelera_confirmacion_mensaje)) },
            confirmButton = {
                Button(
                    onClick = {
                        val notasAEliminar = listaDeNotasGlobal.filter { it.estaEnPapelera }
                        listaDeNotasGlobal.removeAll(notasAEliminar.toSet())
                        // USA context.getString() AQUÍ
                        Toast.makeText(context, context.getString(R.string.papelera_vaciada_toast), Toast.LENGTH_SHORT).show()
                        mostrarDialogoVaciarPapeleraConfirmacion = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.vaciar_boton)) // Aquí stringResource está bien porque Text() es @Composable
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoVaciarPapeleraConfirmacion = false }) {
                    Text(stringResource(R.string.cancelar_boton)) // Aquí stringResource está bien
                }
            }
        )
    }
}

// --- Previews ---
@Preview(showBackground = true, name = "App Principal Preview (Light)")
@Composable
fun PantallaPrincipalNotasAppPreviewLight() {
    Proyecto_ABP_Bloc_de_NotasTheme(darkTheme = false) {
        PantallaPrincipalNotasApp(
            initialDarkTheme = false,
            onThemeChange = {}
        )
    }
}

@Preview(showBackground = true, name = "App Principal Preview (Dark)")
@Composable
fun PantallaPrincipalNotasAppPreviewDark() {
    Proyecto_ABP_Bloc_de_NotasTheme(darkTheme = true) {
        PantallaPrincipalNotasApp(
            initialDarkTheme = true,
            onThemeChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Notas List Preview (Light)")
@Composable
fun DefaultPreviewLight() {
    Proyecto_ABP_Bloc_de_NotasTheme(darkTheme = false) {
        PantallaPrincipalNotas(onNavigateToSettings = {}, onNavigateToAbout = {})
    }
}

@Preview(showBackground = true, name = "Notas List Preview (Dark)")
@Composable
fun DefaultPreviewDark() {
    Proyecto_ABP_Bloc_de_NotasTheme(darkTheme = true) {
        PantallaPrincipalNotas(onNavigateToSettings = {}, onNavigateToAbout = {})
    }
}

// Simulación de PantallaAjustes y PantallaAcercaDe para que las Previews y el código compilen
// Deberías tener estos en sus propios archivos o implementaciones completas.
@OptIn(ExperimentalMaterial3Api::class) // <--- AÑADE O VERIFICA ESTO
@Composable
fun PantallaAjustes(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding -> // El 'padding' que viene de Scaffold content es parte de la API experimental
        Column(
            modifier = Modifier
                .padding(padding) // Usando el padding del Scaffold
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Pantalla de Ajustes")
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tema Oscuro")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(checked = isDarkTheme, onCheckedChange = onThemeChanged)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // <--- AÑADE O VERIFICA ESTO
@Composable
fun PantallaAcercaDe(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerca de") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding) // El padding del Scaffold M3
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Pantalla Acerca De")
            Text("Versión: 1.0.0")
            Text("Año: 2025")
            Text("Autores: Juan Mañanes, David San Martin, Jose David Cabeza")
        }
    }
}