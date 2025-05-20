package com.icr.proyecto_abp_bloc_de_notas

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.icr.proyecto_abp_bloc_de_notas.ui.theme.Proyecto_ABP_Bloc_de_NotasTheme
import com.icr.proyecto_abp_bloc_de_notas.ui.theme.*
import kotlinx.coroutines.launch

data class Nota(
    var id: Int,
    var titulo: String,
    var contenido: String,
    var estaEnPapelera: Boolean = false, // Indica si la nota ha sido enviada a la papelera
    var esImportante: Boolean = false    // Indica si la nota es importante
)

// Enum para gestionar la vista actual (Notas Activas o Papelera)
enum class VistaActualNotas {
    ACTIVAS,
    PAPELERA
}

// Enum para la navegación entre pantallas principales de la aplicación
enum class PantallaAplicacion {
    LISTA_NOTAS,
    AJUSTES,
    ACERCA_DE
}

// Enum para definir el tipo de filtro de búsqueda
enum class TipoFiltroBusqueda {
    TITULO,
    CONTENIDO
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipalNotasApp(
    temaInicialOscuro: Boolean = false,
    alCambiarTema: (Boolean) -> Unit
) {
    var pantallaActualNav by rememberSaveable { mutableStateOf(PantallaAplicacion.LISTA_NOTAS) }
    var esTemaOscuroActual by remember { mutableStateOf(temaInicialOscuro) }

    Proyecto_ABP_Bloc_de_NotasTheme(darkTheme = esTemaOscuroActual) {
        PantallaPrincipalNotas(
            pantallaActual = pantallaActualNav,
            alNavegarAAjustes = { pantallaActualNav = PantallaAplicacion.AJUSTES },
            alNavegarAAcercaDe = { pantallaActualNav = PantallaAplicacion.ACERCA_DE },
            alNavegarAListaNotas = { pantallaActualNav = PantallaAplicacion.LISTA_NOTAS },
            esTemaOscuro = esTemaOscuroActual,
            alCambiarTemaApp = { nuevoEstadoTemaOscuro ->
                esTemaOscuroActual = nuevoEstadoTemaOscuro
                alCambiarTema(nuevoEstadoTemaOscuro)
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PantallaPrincipalNotas(
    pantallaActual: PantallaAplicacion,
    alNavegarAAjustes: () -> Unit,
    alNavegarAAcercaDe: () -> Unit,
    alNavegarAListaNotas: () -> Unit,
    esTemaOscuro: Boolean,
    alCambiarTemaApp: (Boolean) -> Unit
) {
    val estadoCajonNavegacion = rememberDrawerState(initialValue = DrawerValue.Closed)
    val alcanceCoroutine = rememberCoroutineScope()
    val contexto = LocalContext.current

    val listaDeNotasGlobal = remember { mutableStateListOf<Nota>() }
    var contadorIdNotas by rememberSaveable { mutableStateOf(0) }

    var mostrarDialogoEdicionNota by remember { mutableStateOf(false) }
    var notaParaEditarActual by remember { mutableStateOf<Nota?>(null) }
    var tituloEditadoTemporal by remember { mutableStateOf("") }
    var contenidoEditadoTemporal by remember { mutableStateOf("") }
    var esImportanteTemporalDialogo by remember { mutableStateOf(false) } // NUEVO estado para el Checkbox del diálogo

    var indiceElementoSelCajon by rememberSaveable { mutableStateOf(0) }

    var notaParaAccionLargaDialogo by remember { mutableStateOf<Nota?>(null) }
    var mostrarDialogoEnviarPapeleraConf by remember { mutableStateOf(false) }
    var mostrarDialogoVaciarPapeleraConf by remember { mutableStateOf(false) }

    var vistaActualDeNotas by rememberSaveable { mutableStateOf(VistaActualNotas.ACTIVAS) }

    var consultaDeBusqueda by rememberSaveable { mutableStateOf("") }
    var busquedaEstaActiva by rememberSaveable { mutableStateOf(false) }
    var tipoDeFiltroBusqueda by rememberSaveable { mutableStateOf(TipoFiltroBusqueda.TITULO) }
    val administradorDeFoco = LocalFocusManager.current
    var esBusquedaFocused by remember { mutableStateOf(false) }
    var esTituloDialogoFocused by remember { mutableStateOf(false) }
    var esContenidoDialogoFocused by remember { mutableStateOf(false) }

    when (pantallaActual) {
        PantallaAplicacion.AJUSTES -> {
            PantallaAjustes(
                esTemaOscuro = esTemaOscuro,
                alCambiarTema = alCambiarTemaApp,
                alNavegarAtras = alNavegarAListaNotas
            )
            return
        }

        PantallaAplicacion.ACERCA_DE -> {
            PantallaAcercaDe(
                alNavegarAtras = alNavegarAListaNotas
            )
            return
        }

        PantallaAplicacion.LISTA_NOTAS -> {
            // Continúa
        }
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
                    icon = {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = stringResource(R.string.notas_activas)
                        )
                    },
                    label = { Text(stringResource(R.string.notas_activas)) },
                    selected = indiceElementoSelCajon == 0,
                    onClick = {
                        vistaActualDeNotas = VistaActualNotas.ACTIVAS
                        indiceElementoSelCajon = 0
                        alcanceCoroutine.launch { estadoCajonNavegacion.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.papelera)
                        )
                    },
                    label = { Text(stringResource(R.string.papelera)) },
                    selected = indiceElementoSelCajon == 1,
                    onClick = {
                        vistaActualDeNotas = VistaActualNotas.PAPELERA
                        indiceElementoSelCajon = 1
                        alcanceCoroutine.launch { estadoCajonNavegacion.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = stringResource(R.string.acerca_de)
                        )
                    },
                    label = { Text(stringResource(R.string.acerca_de)) },
                    selected = indiceElementoSelCajon == 2,
                    onClick = {
                        indiceElementoSelCajon = 2
                        alcanceCoroutine.launch { estadoCajonNavegacion.close() }
                        alNavegarAAcercaDe()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings_name)
                        )
                    },
                    label = { Text(stringResource(R.string.settings_name)) },
                    selected = indiceElementoSelCajon == 3,
                    onClick = {
                        indiceElementoSelCajon = 3
                        alcanceCoroutine.launch { estadoCajonNavegacion.close() }
                        alNavegarAAjustes()
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
                        if (busquedaEstaActiva) {
                            val focusRequester = remember { FocusRequester() }
                            BasicTextField(
                                value = consultaDeBusqueda,
                                onValueChange = { consultaDeBusqueda = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 0.dp)
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { focusState ->
                                        esBusquedaFocused = focusState.isFocused
                                    }
                                    .background(
                                        if (esBusquedaFocused) SearchFieldFocusedBackgroundColor
                                        else SearchFieldDefaultBackgroundColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {
                                    administradorDeFoco.clearFocus()
                                }),
                                decorationBox = { textFieldInterno ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (consultaDeBusqueda.isEmpty()) {
                                            Text(
                                                text = if (tipoDeFiltroBusqueda == TipoFiltroBusqueda.TITULO) stringResource(
                                                    R.string.buscar_por_titulo
                                                )
                                                else stringResource(R.string.buscar_por_contenido),
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                    alpha = 0.6f
                                                )
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
                                text = if (vistaActualDeNotas == VistaActualNotas.ACTIVAS) stringResource(
                                    R.string.nombre_aplicacion
                                )
                                else stringResource(R.string.papelera)
                            )
                        }
                    },
                    navigationIcon = {
                        if (busquedaEstaActiva) {
                            IconButton(onClick = {
                                busquedaEstaActiva = false
                                consultaDeBusqueda = ""
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cerrar_busqueda_desc)
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                alcanceCoroutine.launch { estadoCajonNavegacion.open() }
                            }) {
                                Icon(
                                    Icons.Filled.Menu,
                                    contentDescription = stringResource(R.string.menu_descripcion)
                                )
                            }
                        }
                    },
                    actions = {
                        if (busquedaEstaActiva) {
                            IconButton(onClick = {
                                tipoDeFiltroBusqueda =
                                    if (tipoDeFiltroBusqueda == TipoFiltroBusqueda.TITULO) TipoFiltroBusqueda.CONTENIDO else TipoFiltroBusqueda.TITULO
                            }) {
                                Icon(
                                    imageVector = if (tipoDeFiltroBusqueda == TipoFiltroBusqueda.TITULO) Icons.Filled.Abc else Icons.AutoMirrored.Filled.Notes,
                                    contentDescription = stringResource(R.string.cambiar_filtro_busqueda_desc)
                                )
                            }
                            if (consultaDeBusqueda.isNotEmpty()) {
                                IconButton(onClick = { consultaDeBusqueda = "" }) {
                                    Icon(
                                        Icons.Filled.Clear,
                                        contentDescription = stringResource(R.string.limpiar_busqueda_desc)
                                    )
                                }
                            }
                        } else {
                            IconButton(onClick = { busquedaEstaActiva = true }) {
                                Icon(
                                    Icons.Filled.Search,
                                    contentDescription = stringResource(R.string.abrir_busqueda_desc)
                                )
                            }
                            IconButton(onClick = alNavegarAAjustes) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = stringResource(R.string.settings_name)
                                )
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
                if (!busquedaEstaActiva) {
                    when (vistaActualDeNotas) {
                        VistaActualNotas.ACTIVAS -> {
                            FloatingActionButton(onClick = {
                                contadorIdNotas++
                                val nuevaNota =
                                    Nota( // Al crear una nueva nota, esImportante es false por defecto
                                        id = contadorIdNotas,
                                        titulo = "${contexto.getString(R.string.nota_nueva_titulo_prefijo)} $contadorIdNotas",
                                        contenido = ""
                                        // esImportante = false // Ya es el valor por defecto en data class
                                    )
                                notaParaEditarActual = nuevaNota
                                tituloEditadoTemporal = nuevaNota.titulo
                                contenidoEditadoTemporal = nuevaNota.contenido
                                esImportanteTemporalDialogo =
                                    nuevaNota.esImportante // Se inicializa con el valor de la nota (false para nuevas)
                                mostrarDialogoEdicionNota = true
                            }) {
                                Icon(Icons.Filled.Add, stringResource(R.string.anadir_nueva_nota))
                            }
                        }

                        VistaActualNotas.PAPELERA -> {
                            if (listaDeNotasGlobal.any { it.estaEnPapelera }) {
                                FloatingActionButton(
                                    onClick = {
                                        mostrarDialogoVaciarPapeleraConf = true
                                    },
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ) {
                                    Icon(
                                        Icons.Filled.DeleteSweep,
                                        contentDescription = stringResource(R.string.vaciar_papelera_descripcion)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingInterno ->

            val notasFiltradasSegunVista = when (vistaActualDeNotas) {
                VistaActualNotas.ACTIVAS -> listaDeNotasGlobal.filter { !it.estaEnPapelera }
                VistaActualNotas.PAPELERA -> listaDeNotasGlobal.filter { it.estaEnPapelera }
            }

            val notasAMostrarEnLista = if (busquedaEstaActiva && consultaDeBusqueda.isNotBlank()) {
                notasFiltradasSegunVista.filter { nota ->
                    when (tipoDeFiltroBusqueda) {
                        TipoFiltroBusqueda.TITULO -> nota.titulo.contains(
                            consultaDeBusqueda,
                            ignoreCase = true
                        )

                        TipoFiltroBusqueda.CONTENIDO -> nota.contenido.contains(
                            consultaDeBusqueda,
                            ignoreCase = true
                        )
                    }
                }
            } else {
                notasFiltradasSegunVista
            }


            if (notasAMostrarEnLista.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(paddingInterno)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (busquedaEstaActiva && consultaDeBusqueda.isNotBlank()) stringResource(R.string.mensaje_no_hay_resultados_busqueda)
                        else if (vistaActualDeNotas == VistaActualNotas.ACTIVAS) stringResource(R.string.mensaje_no_hay_notas)
                        else stringResource(R.string.mensaje_papelera_vacia)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingInterno)
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(notasAMostrarEnLista, key = { it.id }) { nota ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .pointerInput(nota) {
                                    detectTapGestures(
                                        onTap = {
                                            if (vistaActualDeNotas == VistaActualNotas.ACTIVAS && !nota.estaEnPapelera) {
                                                notaParaEditarActual = nota
                                                tituloEditadoTemporal = nota.titulo
                                                contenidoEditadoTemporal = nota.contenido
                                                esImportanteTemporalDialogo =
                                                    nota.esImportante // Carga el estado de importancia
                                                mostrarDialogoEdicionNota = true
                                            } else if (vistaActualDeNotas == VistaActualNotas.PAPELERA && nota.estaEnPapelera) {
                                                Toast
                                                    .makeText(
                                                        contexto,
                                                        "${contexto.getString(R.string.opciones_para_nota_en_papelera)}: ${nota.titulo}",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            }
                                        },
                                        onLongPress = {
                                            if (vistaActualDeNotas == VistaActualNotas.ACTIVAS && !nota.estaEnPapelera) {
                                                notaParaAccionLargaDialogo = nota
                                                mostrarDialogoEnviarPapeleraConf = true
                                            }
                                        }
                                    )
                                }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row( // Row para título e icono de importancia
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        nota.titulo,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f) // Título ocupa espacio disponible
                                    )
                                    if (nota.esImportante) { // Muestra icono si es importante
                                        Icon(
                                            imageVector = Icons.Filled.Star, // Icono de estrella
                                            contentDescription = stringResource(R.string.nota_importante_desc),
                                            tint = MaterialTheme.colorScheme.primary, // Color distintivo
                                            modifier = Modifier.padding(start = 8.dp) // Espacio a la izquierda del icono
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

    // Diálogo de Edición de Nota
    if (mostrarDialogoEdicionNota) {
        val esRealmenteNuevaNota =
            listaDeNotasGlobal.find { it.id == notaParaEditarActual?.id } == null

        AlertDialog(
            onDismissRequest = {
                mostrarDialogoEdicionNota = false
                notaParaEditarActual = null
            },
            title = {
                Text(
                    if (esRealmenteNuevaNota || notaParaEditarActual?.titulo?.startsWith(
                            stringResource(R.string.nota_nueva_titulo_prefijo)
                        ) == true
                    ) stringResource(R.string.crear_nota_titulo) else stringResource(R.string.editar_nota_titulo)
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = tituloEditadoTemporal,
                        onValueChange = { tituloEditadoTemporal = it },
                        label = { Text(stringResource(R.string.titulo_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                esTituloDialogoFocused = focusState.isFocused
                            },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors( // <--- NUEVO
                            focusedContainerColor = TextFieldFocusedBackgroundColor,
                            unfocusedContainerColor = TextFieldDefaultBackgroundColor,
                            focusedBorderColor = TextFieldFocusedBorderColor,
                            unfocusedBorderColor = TextFieldDefaultBorderColor,
                            focusedLabelColor = TextFieldFocusedLabelColor,
                            unfocusedLabelColor = TextFieldDefaultLabelColor,
                            focusedTextColor = TextFieldFocusedContentColor,
                            unfocusedTextColor = TextFieldDefaultContentColor
                            // Puedes añadir más personalizaciones de color si es necesario
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = contenidoEditadoTemporal,
                        onValueChange = { contenidoEditadoTemporal = it },
                        label = { Text(stringResource(R.string.contenido_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .onFocusChanged { focusState ->
                                esContenidoDialogoFocused = focusState.isFocused
                            },
                        colors = OutlinedTextFieldDefaults.colors( // <--- NUEVO
                            focusedContainerColor = TextFieldFocusedBackgroundColor,
                            unfocusedContainerColor = TextFieldDefaultBackgroundColor,
                            focusedBorderColor = TextFieldFocusedBorderColor,
                            unfocusedBorderColor = TextFieldDefaultBorderColor,
                            focusedLabelColor = TextFieldFocusedLabelColor,
                            unfocusedLabelColor = TextFieldDefaultLabelColor,
                            focusedTextColor = TextFieldFocusedContentColor,
                            unfocusedTextColor = TextFieldDefaultContentColor
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp)) // Espacio antes del Checkbox
                    Row( // Row para el Checkbox y su etiqueta
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                esImportanteTemporalDialogo = !esImportanteTemporalDialogo
                            } // Hace toda la fila clickeable
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = esImportanteTemporalDialogo,
                            onCheckedChange = { esImportanteTemporalDialogo = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.marcar_como_importante_label))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        notaParaEditarActual?.let { notaActual ->
                            val indice = listaDeNotasGlobal.indexOfFirst { it.id == notaActual.id }
                            val tituloFinal =
                                tituloEditadoTemporal.ifBlank { contexto.getString(R.string.nota_sin_titulo) }

                            if (indice != -1) { // Editando nota existente
                                listaDeNotasGlobal[indice] = notaActual.copy(
                                    titulo = tituloFinal,
                                    contenido = contenidoEditadoTemporal,
                                    esImportante = esImportanteTemporalDialogo // Guarda el estado de importancia
                                )
                            } else { // Creando nueva nota (o actualizando la que se añadió desde el FAB)
                                val notaAGuardar = notaActual.copy(
                                    titulo = tituloFinal,
                                    contenido = contenidoEditadoTemporal,
                                    esImportante = esImportanteTemporalDialogo // Guarda el estado de importancia
                                )
                                if (listaDeNotasGlobal.none { it.id == notaAGuardar.id }) {
                                    listaDeNotasGlobal.add(notaAGuardar)
                                } else {
                                    val indiceExistente =
                                        listaDeNotasGlobal.indexOfFirst { it.id == notaAGuardar.id }
                                    if (indiceExistente != -1) listaDeNotasGlobal[indiceExistente] =
                                        notaAGuardar
                                }
                            }
                        }
                        mostrarDialogoEdicionNota = false
                        notaParaEditarActual = null
                    }
                ) { Text(stringResource(R.string.guardar_boton)) }
            },
            dismissButton = {
                Button(onClick = {
                    mostrarDialogoEdicionNota = false
                    notaParaEditarActual = null
                }) { Text(stringResource(R.string.cancelar_boton)) }
            }
        )
    }

    // Diálogos de confirmación (Enviar a papelera, Vaciar papelera) - sin cambios en su lógica interna
    if (mostrarDialogoEnviarPapeleraConf && notaParaAccionLargaDialogo != null) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoEnviarPapeleraConf = false
                notaParaAccionLargaDialogo = null
            },
            icon = { Icon(Icons.Filled.DeleteSweep, contentDescription = null) },
            title = { Text(stringResource(R.string.enviar_a_papelera_titulo)) },
            text = {
                Text(
                    stringResource(
                        R.string.confirmar_enviar_a_papelera_mensaje,
                        notaParaAccionLargaDialogo?.titulo ?: ""
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        notaParaAccionLargaDialogo?.let { notaAMover ->
                            val indice = listaDeNotasGlobal.indexOfFirst { it.id == notaAMover.id }
                            if (indice != -1) {
                                listaDeNotasGlobal[indice] = notaAMover.copy(estaEnPapelera = true)
                                Toast.makeText(
                                    contexto,
                                    contexto.getString(R.string.nota_enviada_a_papelera_toast),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        mostrarDialogoEnviarPapeleraConf = false
                        notaParaAccionLargaDialogo = null
                    }
                ) { Text(stringResource(R.string.enviar_boton)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoEnviarPapeleraConf = false
                    notaParaAccionLargaDialogo = null
                }) { Text(stringResource(R.string.cancelar_boton)) }
            }
        )
    }

    if (mostrarDialogoVaciarPapeleraConf) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoVaciarPapeleraConf = false },
            icon = {
                Icon(
                    Icons.Filled.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text(stringResource(R.string.vaciar_papelera_confirmacion_titulo)) },
            text = { Text(stringResource(R.string.vaciar_papelera_confirmacion_mensaje)) },
            confirmButton = {
                Button(
                    onClick = {
                        val notasAEliminar = listaDeNotasGlobal.filter { it.estaEnPapelera }
                        listaDeNotasGlobal.removeAll(notasAEliminar.toSet())
                        Toast.makeText(
                            contexto,
                            contexto.getString(R.string.papelera_vaciada_toast),
                            Toast.LENGTH_SHORT
                        ).show()
                        mostrarDialogoVaciarPapeleraConf = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.vaciar_boton))
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoVaciarPapeleraConf = false }) {
                    Text(stringResource(R.string.cancelar_boton))
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "App Principal (Notas) Preview (Light)")
@Composable
fun PantallaPrincipalNotasPreviewLight() {
    Proyecto_ABP_Bloc_de_NotasTheme(darkTheme = false) {
        PantallaPrincipalNotas(
            pantallaActual = PantallaAplicacion.LISTA_NOTAS,
            alNavegarAAjustes = {},
            alNavegarAAcercaDe = {},
            alNavegarAListaNotas = {},
            esTemaOscuro = false,
            alCambiarTemaApp = {}
        )
    }
}

@Preview(showBackground = true, name = "App Principal (Notas) Preview (Dark)")
@Composable
fun PantallaPrincipalNotasPreviewDark() {
    Proyecto_ABP_Bloc_de_NotasTheme(darkTheme = true) {
        PantallaPrincipalNotas(
            pantallaActual = PantallaAplicacion.LISTA_NOTAS,
            alNavegarAAjustes = {},
            alNavegarAAcercaDe = {},
            alNavegarAListaNotas = {},
            esTemaOscuro = true,
            alCambiarTemaApp = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.volver_descripcion))
                    }
                }
            )
        }
    ) { paddingInternoScaffold ->
        Column(
            modifier = Modifier
                .padding(paddingInternoScaffold)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.tema_oscuro_label))
                Spacer(modifier = Modifier.weight(1f))
                Switch(checked = esTemaOscuro, onCheckedChange = alCambiarTema)
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.volver_descripcion))
                    }
                }
            )
        }
    ) { paddingInternoScaffold ->
        Column(
            modifier = Modifier
                .padding(paddingInternoScaffold)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.nombre_aplicacion),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("${stringResource(R.string.about_version_name_label)}: 1.0.0")
            Spacer(modifier = Modifier.height(4.dp))
            Text("Autores: Juan Mañanes, David San Martin, Jose David Cabeza")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(
                    R.string.about_copyright,
                    java.util.Calendar.getInstance().get(java.util.Calendar.YEAR).toString()
                )
            )
        }
    }
}