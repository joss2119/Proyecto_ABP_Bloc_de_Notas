package com.icr.proyecto_abp_bloc_de_notas

// Importa NotasRepository desde donde esté (ej. data)
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.icr.proyecto_abp_bloc_de_notas.data.UserPreferencesRepository // Asegúrate que la ruta sea correcta
// ELIMINADA: import com.icr.proyecto_abp_bloc_de_notas.data.dataStore // <--- CORRECCIÓN 1
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel para manejar la lógica de las notas y el tema
class MainViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notasRepository: NotasRepository
) : ViewModel() {

    val temaOscuroUiState: StateFlow<Boolean> = userPreferencesRepository.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun guardarPreferenciaTema(esOscuro: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateDarkThemePreference(esOscuro)
        }
    }

    val notasUiState: StateFlow<List<Nota>> = notasRepository.listaNotasFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val contadorIdNotasState: StateFlow<Int> = notasRepository.contadorIdNotasFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    fun guardarListaNotas(notas: List<Nota>) {
        viewModelScope.launch {
            notasRepository.guardarNotas(notas)
        }
    }

    fun guardarContadorIdNotas(contador: Int) {
        viewModelScope.launch {
            notasRepository.guardarContadorIdNotas(contador)
        }
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var notasRepository: NotasRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        userPreferencesRepository = UserPreferencesRepository(this)
        notasRepository = NotasRepository(applicationContext)

        setContent {
            val mainViewModel: MainViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                            return MainViewModel(userPreferencesRepository, notasRepository) as T
                        }
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                    }
                }
            )

            val esTemaOscuroActual by mainViewModel.temaOscuroUiState.collectAsState()

            PantallaPrincipalNotasApp(
                temaInicialOscuro = esTemaOscuroActual,
                alCambiarTema = { nuevoEstado ->
                    mainViewModel.guardarPreferenciaTema(nuevoEstado)
                },
                mainViewModel = mainViewModel
            )
        }
    }
}