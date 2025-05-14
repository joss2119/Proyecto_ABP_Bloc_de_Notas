import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.icr.proyecto_abp_bloc_de_notas.R
import com.icr.proyecto_abp_bloc_de_notas.ui.theme.Proyecto_ABP_Bloc_de_NotasTheme
import kotlinx.coroutines.launch

@Composable
fun PantallaPrincipalNotas() {
    val estadoCajonNavegacion = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    ModalNavigationDrawer(
        drawerState = estadoCajonNavegacion,
        drawerContent = {
            ModalDrawerSheet {
                Text(stringResource(R.string.titulo_cajon), modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                // Añadir elementos de navegacion aqui
            }
        }
    ) {
        Scaffold(
            topBar = {
                // Usamos una columna para crear un layout de barra superior custom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        scope.launch {
                            estadoCajonNavegacion.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu"
                        )
                    }
                    // Nombre de aplicaciones
                    Text(
                        stringResource(R.string.nombre_aplicacion),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )

                    // Icono de ajustes
                    IconButton(
                        onClick = {
                            // TODO: Manejar click del icono
                        }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings_name)
                        )
                    }

                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    // TODO: Manejar click del FAB para crear una nueva nota, etc.
                }) {
                    Icon(Icons.Filled.Add, "Añadir nueva nota")
                }
            },
            floatingActionButtonPosition = FabPosition.End // Colocar abajo a la derecha
        ) { innerPadding -> // Contenido principal de la pantalla
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Text("Aqui iran tus notas") // TODO: Placeholder de las notas
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaPrincipalNotasPreview() {
    Proyecto_ABP_Bloc_de_NotasTheme {
        PantallaPrincipalNotas()
    }
}