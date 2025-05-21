@file:OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)

package com.icr.proyecto_abp_bloc_de_notas

import android.annotation.SuppressLint
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import com.icr.proyecto_abp_bloc_de_notas.data.UserPreferencesRepository
import com.icr.proyecto_abp_bloc_de_notas.ui.theme.Proyecto_ABP_Bloc_de_NotasTheme
import com.icr.proyecto_abp_bloc_de_notas.ui.theme.SearchFieldDefaultBackgroundColor
import com.icr.proyecto_abp_bloc_de_notas.ui.theme.SearchFieldFocusedBackgroundColor
import com.icr.proyecto_abp_bloc_de_notas.ui.theme.TextFieldDefaultBackgroundColor
import com.icr.proyecto_abp_bloc_de_notas.ui.theme.TextFieldFocusedBackgroundColor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.Calendar

@Parcelize
@Serializable
data class Nota(
    var id: Int,
    var titulo: String,
    var contenido: String,
    var estaEnPapelera: Boolean = false,
    var esImportante: Boolean = false
) : Parcelable

enum class VistaActualNotas { ACTIVAS, PAPELERA }
enum class PantallaAplicacion { LISTA_NOTAS, AJUSTES, ACERCA_DE }
enum class TipoFiltroBusqueda { TITULO, CONTENIDO }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipalNotasApp(
    temaInicialOscuro: Boolean,
    alCambiarTema: (Boolean) -> Unit,
    mainViewModel: MainViewModel
) {
    var pantallaNavegacionActual by rememberSaveable { mutableStateOf(PantallaAplicacion.LISTA_NOTAS) }

    Proyecto_ABP_Bloc_de_NotasTheme(darkTheme = temaInicialOscuro) {
        PantallaPrincipalNotas(
            pantallaAppActual = pantallaNavegacionActual,
            alNavegarAAjustes = { pantallaNavegacionActual = PantallaAplicacion.AJUSTES },
            alNavegarAAcercaDe = { pantallaNavegacionActual = PantallaAplicacion.ACERCA_DE },
            alNavegarAListaNotas = { pantallaNavegacionActual = PantallaAplicacion.LISTA_NOTAS },
            esTemaOscuro = temaInicialOscuro,
            alCambiarTemaApp = alCambiarTema,
            mainViewModel = mainViewModel
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PantallaPrincipalNotas(
    pantallaAppActual: PantallaAplicacion,
    alNavegarAAjustes: () -> Unit,
    alNavegarAAcercaDe: () -> Unit,
    alNavegarAListaNotas: () -> Unit,
    esTemaOscuro: Boolean,
    alCambiarTemaApp: (Boolean) -> Unit,
    mainViewModel: MainViewModel
) {
    val estadoCajonNavegacion = rememberDrawerState(initialValue = DrawerValue.Closed)
    val alcanceCoroutine = rememberCoroutineScope()
    val contexto = LocalContext.current

    val notasDesdeViewModel by mainViewModel.notasUiState.collectAsState()
    val contadorDesdeViewModel by mainViewModel.contadorIdNotasState.collectAsState()

    var listaGlobalNotas by remember(notasDesdeViewModel) {
        Log.d("PantallaPrincipal", "Reinicializando listaGlobalNotas local desde ViewModel. Tamaño VM: ${notasDesdeViewModel.size}")
        mutableStateOf(notasDesdeViewModel.toMutableStateList())
    }

    var contadorIdNotas by remember(contadorDesdeViewModel) {
        Log.d("PantallaPrincipal", "Reinicializando contadorIdNotas local desde ViewModel. Valor VM: $contadorDesdeViewModel")
        mutableIntStateOf(contadorDesdeViewModel)
    }

    LaunchedEffect(mainViewModel, listaGlobalNotas) {
        snapshotFlow { listaGlobalNotas.toList() }
            .debounce(500L)
            .collect { listaParaGuardar ->
                Log.d("Persistencia", "SnapshotFlow (lista) disparado. Guardando ${listaParaGuardar.size} notas.")
                mainViewModel.guardarListaNotas(listaParaGuardar)
            }
    }

    LaunchedEffect(mainViewModel, contadorIdNotas) {
        snapshotFlow { contadorIdNotas }
            .debounce(500L)
            .collect { contadorParaGuardar ->
                Log.d("Persistencia", "SnapshotFlow (contador) disparado. Guardando contador $contadorParaGuardar.")
                mainViewModel.guardarContadorIdNotas(contadorParaGuardar)
            }
    }

    var mostrarDialogoEdicionNota by remember { mutableStateOf(false) }
    var notaSeleccionadaParaEditar by remember { mutableStateOf<Nota?>(null) }
    var tituloTemporal by remember { mutableStateOf("") }
    var contenidoTemporal by remember { mutableStateOf("") }
    var esImportanteTemporal by remember { mutableStateOf(false) }

    var itemSeleccionadoCajon by rememberSaveable { mutableIntStateOf(0) }

    var notaParaAccionLarga by remember { mutableStateOf<Nota?>(null) }
    var mostrarDialogoConfirmarMoverAPapelera by remember { mutableStateOf(false) }
    var mostrarDialogoConfirmarVaciarPapelera by remember { mutableStateOf(false) }

    var vistaNotasActual by rememberSaveable { mutableStateOf(VistaActualNotas.ACTIVAS) }

    var consultaBusqueda by rememberSaveable { mutableStateOf("") }
    var busquedaActiva by rememberSaveable { mutableStateOf(false) }
    var filtroBusquedaActual by rememberSaveable { mutableStateOf(TipoFiltroBusqueda.TITULO) }
    val administradorFoco = LocalFocusManager.current
    var campoBusquedaConFoco by remember { mutableStateOf(false) }
    var campoTituloDialogoConFoco by remember { mutableStateOf(false) }
    var campoContenidoDialogoConFoco by remember { mutableStateOf(false) }


    when (pantallaAppActual) {
        PantallaAplicacion.AJUSTES -> {
            PantallaAjustes(
                esTemaOscuro = esTemaOscuro,
                alCambiarTema = alCambiarTemaApp,
                alNavegarAtras = alNavegarAListaNotas
            )
            return
        }
        PantallaAplicacion.ACERCA_DE -> {
            PantallaAcercaDe(alNavegarAtras = alNavegarAListaNotas)
            return
        }
        PantallaAplicacion.LISTA_NOTAS -> { /* Continúa */ }
    }

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
                    selected = itemSeleccionadoCajon == 0,
                    onClick = {
                        vistaNotasActual = VistaActualNotas.ACTIVAS
                        itemSeleccionadoCajon = 0
                        alcanceCoroutine.launch { estadoCajonNavegacion.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.papelera)) },
                    label = { Text(stringResource(R.string.papelera)) },
                    selected = itemSeleccionadoCajon == 1,
                    onClick = {
                        vistaNotasActual = VistaActualNotas.PAPELERA
                        itemSeleccionadoCajon = 1
                        alcanceCoroutine.launch { estadoCajonNavegacion.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.acerca_de)) },
                    label = { Text(stringResource(R.string.acerca_de)) },
                    selected = itemSeleccionadoCajon == 2,
                    onClick = {
                        itemSeleccionadoCajon = 2
                        alcanceCoroutine.launch { estadoCajonNavegacion.close() }
                        alNavegarAAcercaDe()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_name)) },
                    label = { Text(stringResource(R.string.settings_name)) },
                    selected = itemSeleccionadoCajon == 3,
                    onClick = {
                        itemSeleccionadoCajon = 3
                        alcanceCoroutine.launch { estadoCajonNavegacion.close() }
                        alNavegarAAjustes()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.height(16.dp))
            }
        }
        // --- CORRECCIÓN 2 (y relacionadas): Lambda content del ModalNavigationDrawer NO toma parámetros ---
    ) { // Esta es la lambda content del ModalNavigationDrawer
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (busquedaActiva) {
                            val focusRequester = remember { FocusRequester() }
                            BasicTextField(
                                value = consultaBusqueda,
                                onValueChange = { consultaBusqueda = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { estadoFoco -> campoBusquedaConFoco = estadoFoco.isFocused }
                                    .background(
                                        if (campoBusquedaConFoco) SearchFieldFocusedBackgroundColor
                                        else SearchFieldDefaultBackgroundColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = { administradorFoco.clearFocus() }),
                                decorationBox = { textFieldInterno ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (consultaBusqueda.isEmpty()) {
                                            Text(
                                                text = if (filtroBusquedaActual == TipoFiltroBusqueda.TITULO) stringResource(R.string.buscar_por_titulo)
                                                else stringResource(R.string.buscar_por_contenido),
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                            )
                                        }
                                        textFieldInterno()
                                    }
                                }
                            )
                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                            }
                        } else {
                            Text(
                                text = if (vistaNotasActual == VistaActualNotas.ACTIVAS) stringResource(R.string.nombre_aplicacion)
                                else stringResource(R.string.papelera)
                            )
                        }
                    },
                    navigationIcon = {
                        if (busquedaActiva) {
                            IconButton(onClick = {
                                busquedaActiva = false
                                consultaBusqueda = ""
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cerrar_busqueda_desc))
                            }
                        } else {
                            IconButton(onClick = { alcanceCoroutine.launch { estadoCajonNavegacion.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.menu_descripcion))
                            }
                        }
                    },
                    actions = {
                        if (busquedaActiva) {
                            IconButton(onClick = {
                                filtroBusquedaActual = if (filtroBusquedaActual == TipoFiltroBusqueda.TITULO) TipoFiltroBusqueda.CONTENIDO else TipoFiltroBusqueda.TITULO
                            }) {
                                Icon(
                                    imageVector = if (filtroBusquedaActual == TipoFiltroBusqueda.TITULO) Icons.Filled.Abc else Icons.AutoMirrored.Filled.Notes,
                                    contentDescription = stringResource(R.string.cambiar_filtro_busqueda_desc)
                                )
                            }
                            if (consultaBusqueda.isNotEmpty()) {
                                IconButton(onClick = { consultaBusqueda = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.limpiar_busqueda_desc))
                                }
                            }
                        } else {
                            IconButton(onClick = { busquedaActiva = true }) {
                                Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.abrir_busqueda_desc))
                            }
                            IconButton(onClick = alNavegarAAjustes) {
                                Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_name))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            floatingActionButton = {
                if (!busquedaActiva) {
                    when (vistaNotasActual) {
                        VistaActualNotas.ACTIVAS -> {
                            FloatingActionButton(onClick = {
                                val nuevoId = contadorIdNotas + 1
                                contadorIdNotas = nuevoId
                                Log.d("FAB_Click", "Nuevo ID generado: $nuevoId, Contador actualizado a: $contadorIdNotas")

                                val nuevaNota = Nota(
                                    id = nuevoId,
                                    titulo = contexto.getString(R.string.nota_nueva_titulo_dinamico, nuevoId),
                                    contenido = ""
                                )
                                notaSeleccionadaParaEditar = nuevaNota
                                tituloTemporal = nuevaNota.titulo
                                contenidoTemporal = nuevaNota.contenido
                                esImportanteTemporal = nuevaNota.esImportante
                                mostrarDialogoEdicionNota = true
                            }) {
                                Icon(Icons.Filled.Add, stringResource(R.string.anadir_nueva_nota))
                            }
                        }
                        VistaActualNotas.PAPELERA -> {
                            if (listaGlobalNotas.any { it.estaEnPapelera }) {
                                FloatingActionButton(
                                    onClick = { mostrarDialogoConfirmarVaciarPapelera = true },
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ) {
                                    Icon(Icons.Filled.DeleteSweep, contentDescription = stringResource(R.string.vaciar_papelera_descripcion))
                                }
                            }
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingDelScaffold -> // ESTE es el parámetro PaddingValues del Scaffold

            val notasFiltradasPorVistaActual = when (vistaNotasActual) {
                VistaActualNotas.ACTIVAS -> listaGlobalNotas.filter { !it.estaEnPapelera }
                VistaActualNotas.PAPELERA -> listaGlobalNotas.filter { it.estaEnPapelera }
            }

            val notasFinalesParaMostrar = if (busquedaActiva && consultaBusqueda.isNotBlank()) {
                notasFiltradasPorVistaActual.filter { nota ->
                    when (filtroBusquedaActual) {
                        TipoFiltroBusqueda.TITULO -> nota.titulo.contains(consultaBusqueda, ignoreCase = true)
                        TipoFiltroBusqueda.CONTENIDO -> nota.contenido.contains(consultaBusqueda, ignoreCase = true)
                    }
                }
            } else {
                notasFiltradasPorVistaActual
            }

            if (notasFinalesParaMostrar.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(paddingDelScaffold) // Usar el padding del Scaffold aquí
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (busquedaActiva && consultaBusqueda.isNotBlank()) stringResource(R.string.mensaje_no_hay_resultados_busqueda)
                        else if (vistaNotasActual == VistaActualNotas.ACTIVAS) stringResource(R.string.mensaje_no_hay_notas)
                        else stringResource(R.string.mensaje_papelera_vacia),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingDelScaffold) // Usar el padding del Scaffold aquí
                        .fillMaxSize()
                        .padding(8.dp) // Padding adicional si es necesario para el contenido interno de la lista
                ) {
                    items(notasFinalesParaMostrar, key = { it.id }) { nota ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .pointerInput(nota) {
                                    detectTapGestures(
                                        onTap = {
                                            if (vistaNotasActual == VistaActualNotas.ACTIVAS && !nota.estaEnPapelera) {
                                                notaSeleccionadaParaEditar = nota
                                                tituloTemporal = nota.titulo
                                                contenidoTemporal = nota.contenido
                                                esImportanteTemporal = nota.esImportante
                                                mostrarDialogoEdicionNota = true
                                            } else if (vistaNotasActual == VistaActualNotas.PAPELERA && nota.estaEnPapelera) {
                                                Toast.makeText(
                                                    contexto,
                                                    contexto.getString(R.string.opciones_papelera_nota_toast, nota.titulo),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        onLongPress = {
                                            if (vistaNotasActual == VistaActualNotas.ACTIVAS && !nota.estaEnPapelera) {
                                                notaParaAccionLarga = nota
                                                mostrarDialogoConfirmarMoverAPapelera = true
                                            }
                                        }
                                    )
                                }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        nota.titulo,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (nota.esImportante) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = stringResource(R.string.nota_importante_desc),
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    nota.contenido,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 3
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

    if (mostrarDialogoEdicionNota) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoEdicionNota = false
                notaSeleccionadaParaEditar = null
            },
            title = {
                Text(
                    // Determina si es crear o editar basado en si la nota (por ID) ya existe en la lista actual
                    if (notaSeleccionadaParaEditar != null && listaGlobalNotas.any { it.id == notaSeleccionadaParaEditar!!.id })
                        stringResource(R.string.editar_nota_titulo)
                    else stringResource(R.string.crear_nota_titulo)
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = tituloTemporal,
                        onValueChange = { tituloTemporal = it },
                        label = { Text(stringResource(R.string.titulo_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { estadoFoco -> campoTituloDialogoConFoco = estadoFoco.isFocused },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = TextFieldFocusedBackgroundColor,
                            unfocusedContainerColor = TextFieldDefaultBackgroundColor,
                            // ...otros colores...
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = contenidoTemporal,
                        onValueChange = { contenidoTemporal = it },
                        label = { Text(stringResource(R.string.contenido_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .onFocusChanged { estadoFoco -> campoContenidoDialogoConFoco = estadoFoco.isFocused },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = TextFieldFocusedBackgroundColor,
                            unfocusedContainerColor = TextFieldDefaultBackgroundColor,
                            // ...otros colores...
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { esImportanteTemporal = !esImportanteTemporal }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = esImportanteTemporal,
                            onCheckedChange = { esImportanteTemporal = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.marcar_como_importante_label))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        notaSeleccionadaParaEditar?.let { notaActual ->
                            val tituloFinal = tituloTemporal.ifBlank { contexto.getString(R.string.nota_sin_titulo) }
                            val notaModificada = notaActual.copy( // usa notaActual (que tiene el ID correcto del FAB o de la nota existente)
                                titulo = tituloFinal,
                                contenido = contenidoTemporal,
                                esImportante = esImportanteTemporal
                            )

                            val indice = listaGlobalNotas.indexOfFirst { it.id == notaModificada.id }
                            if (indice != -1) { // La nota (por ID) ya existe, actualízala
                                listaGlobalNotas[indice] = notaModificada
                                Log.d("DialogoEdicion", "Nota ACTUALIZADA: ID ${notaModificada.id}")
                            } else { // La nota no existe en la lista, añádela (esto cubre las nuevas notas del FAB)
                                listaGlobalNotas.add(notaModificada)
                                Log.d("DialogoEdicion", "Nota NUEVA AÑADIDA: ID ${notaModificada.id}")
                            }
                        }
                        mostrarDialogoEdicionNota = false
                        notaSeleccionadaParaEditar = null
                    }
                ) { Text(stringResource(R.string.guardar_boton)) }
            },
            dismissButton = {
                Button(onClick = {
                    mostrarDialogoEdicionNota = false
                    notaSeleccionadaParaEditar = null
                }) { Text(stringResource(R.string.cancelar_boton)) }
            }
        )
    }

    if (mostrarDialogoConfirmarMoverAPapelera && notaParaAccionLarga != null) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoConfirmarMoverAPapelera = false
                notaParaAccionLarga = null
            },
            icon = { Icon(Icons.Filled.DeleteSweep, contentDescription = null) },
            title = { Text(stringResource(R.string.enviar_a_papelera_titulo)) },
            text = { Text(stringResource(R.string.confirmar_enviar_a_papelera_mensaje, notaParaAccionLarga?.titulo ?: "")) },
            confirmButton = {
                Button(
                    onClick = {
                        notaParaAccionLarga?.let { notaAMover ->
                            val indice = listaGlobalNotas.indexOfFirst { it.id == notaAMover.id }
                            if (indice != -1) {
                                listaGlobalNotas[indice] = listaGlobalNotas[indice].copy(estaEnPapelera = true)
                                Toast.makeText(contexto, contexto.getString(R.string.nota_enviada_a_papelera_toast), Toast.LENGTH_SHORT).show()
                            }
                        }
                        mostrarDialogoConfirmarMoverAPapelera = false
                        notaParaAccionLarga = null
                    }
                ) { Text(stringResource(R.string.enviar_boton)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoConfirmarMoverAPapelera = false
                    notaParaAccionLarga = null
                }) { Text(stringResource(R.string.cancelar_boton)) }
            }
        )
    }

    if (mostrarDialogoConfirmarVaciarPapelera) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoConfirmarVaciarPapelera = false },
            icon = { Icon(Icons.Filled.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text(stringResource(R.string.vaciar_papelera_confirmacion_titulo)) },
            text = { Text(stringResource(R.string.vaciar_papelera_confirmacion_mensaje)) },
            confirmButton = {
                Button(
                    onClick = {
                        val notasAEliminar = listaGlobalNotas.filter { it.estaEnPapelera }
                        listaGlobalNotas.removeAll(notasAEliminar.toSet())
                        Toast.makeText(contexto, contexto.getString(R.string.papelera_vaciada_toast), Toast.LENGTH_SHORT).show()
                        mostrarDialogoConfirmarVaciarPapelera = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.vaciar_boton)) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoConfirmarVaciarPapelera = false }) { Text(stringResource(R.string.cancelar_boton)) }
            }
        )
    }
}


@Composable
fun PantallaAjustes(
    esTemaOscuro: Boolean,
    alCambiarTema: (Boolean) -> Unit,
    alNavegarAtras: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_name)) },
                navigationIcon = {
                    IconButton(onClick = alNavegarAtras) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.volver_descripcion)
                        )
                    }
                }
            )
        },
    ) { paddingDelScaffold -> // Cambiado el nombre del parámetro para claridad
        ConstraintLayout(
            modifier = Modifier
                .padding(paddingDelScaffold) // Usar el padding del Scaffold aquí
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val (etiquetaTema, selectorTema, botonAyudaAjustes) = createRefs()
            var mostrarDialogoAyudaAjustes by remember { mutableStateOf(false) }

            Text(
                text = stringResource(R.string.tema_oscuro_label),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.constrainAs(etiquetaTema) {
                    start.linkTo(parent.start)
                    centerVerticallyTo(selectorTema)
                }
            )

            Switch(
                checked = esTemaOscuro,
                onCheckedChange = alCambiarTema,
                modifier = Modifier.constrainAs(selectorTema) {
                    top.linkTo(parent.top)
                }
            )

            TextButton(
                onClick = { mostrarDialogoAyudaAjustes = true },
                modifier = Modifier.constrainAs(botonAyudaAjustes) {
                    end.linkTo(parent.end)
                    centerVerticallyTo(selectorTema)
                }
            ) {
                Text(stringResource(R.string.boton_ayuda_ajustes))
            }
            createHorizontalChain(etiquetaTema, selectorTema, botonAyudaAjustes, chainStyle = ChainStyle.Spread)

            if (mostrarDialogoAyudaAjustes) {
                BasicAlertDialog(onDismissRequest = { mostrarDialogoAyudaAjustes = false }) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        tonalElevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.ayuda_de_la_aplicacion_basico_text),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = stringResource(R.string.ayuda_ajustes_1),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { mostrarDialogoAyudaAjustes = false }) {
                                    Text(stringResource(R.string.entendido_ayuda_ajustes))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAcercaDe(alNavegarAtras: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.acerca_de_titulo_pantalla)) },
                navigationIcon = {
                    IconButton(onClick = alNavegarAtras) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.volver_descripcion)
                        )
                    }
                }
            )
        }
    ) { paddingDelScaffold -> // Cambiado el nombre del parámetro para claridad
        Column(
            modifier = Modifier
                .padding(paddingDelScaffold) // Usar el padding del Scaffold aquí
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.nombre_aplicacion),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("${stringResource(R.string.about_version_code_label)}: ${stringResource(R.string.version_valor)}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stringResource(
                    R.string.autores_texto,
                    stringResource(R.string.autores_nombres)
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(
                    R.string.about_copyright,
                    Calendar.getInstance().get(Calendar.YEAR).toString()
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "App Principal (Notas) Preview (Light)")
@Composable
fun PantallaPrincipalNotasPreviewLight() {
    val mockViewModel = MainViewModel(
        UserPreferencesRepository(LocalContext.current),
        NotasRepository(LocalContext.current)
    )
    Proyecto_ABP_Bloc_de_NotasTheme(darkTheme = false) {
        PantallaPrincipalNotas(
            pantallaAppActual = PantallaAplicacion.LISTA_NOTAS,
            alNavegarAAjustes = {},
            alNavegarAAcercaDe = {},
            alNavegarAListaNotas = {},
            esTemaOscuro = false,
            alCambiarTemaApp = {},
            mainViewModel = mockViewModel
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "App Principal (Notas) Preview (Dark)")
@Composable
fun PantallaPrincipalNotasPreviewDark() {
    val mockViewModel = MainViewModel(
        UserPreferencesRepository(LocalContext.current),
        NotasRepository(LocalContext.current)
    )
    Proyecto_ABP_Bloc_de_NotasTheme(darkTheme = true) {
        PantallaPrincipalNotas(
            pantallaAppActual = PantallaAplicacion.LISTA_NOTAS,
            alNavegarAAjustes = {},
            alNavegarAAcercaDe = {},
            alNavegarAListaNotas = {},
            esTemaOscuro = true,
            alCambiarTemaApp = {},
            mainViewModel = mockViewModel
        )
    }
}