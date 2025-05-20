package com.icr.proyecto_abp_bloc_de_notas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState // Para coleccionar el Flow como State
import androidx.compose.runtime.getValue
// Quita mutableStateOf, remember y setValue si ya no los usas para esTemaOscuroPreferido directamente aquí
// import androidx.compose.runtime.mutableStateOf
// import androidx.compose.runtime.remember
// import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope // Para lanzar coroutines ligadas al ciclo de vida
import com.icr.proyecto_abp_bloc_de_notas.data.UserPreferencesRepository // Asegúrate que la ruta sea correcta
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var userPreferencesRepository: UserPreferencesRepository // Declara la variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el repositorio
        userPreferencesRepository = UserPreferencesRepository(applicationContext)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)

        setContent {
            // Lee la preferencia del tema desde DataStore y la observa como un State de Compose
            // El valor inicial será 'false' hasta que DataStore emita el primer valor.
            val esTemaOscuroPreferido by userPreferencesRepository.isDarkTheme.collectAsState(
                initial = false // Valor inicial mientras se carga desde DataStore
            )

            LaunchedEffect(esTemaOscuroPreferido) {
                val esClaro = !esTemaOscuroPreferido
                insetsController.isAppearanceLightStatusBars = esClaro
                insetsController.isAppearanceLightNavigationBars = esClaro
            }

            PantallaPrincipalNotasApp(
                temaInicialOscuro = esTemaOscuroPreferido,
                alCambiarTema = { nuevoEstadoOscuro ->
                    // Cuando el tema cambia, lo guardamos en DataStore
                    // Usamos lifecycleScope para lanzar la coroutine
                    lifecycleScope.launch {
                        userPreferencesRepository.updateDarkThemePreference(nuevoEstadoOscuro)
                    }
                    // No necesitamos actualizar 'esTemaOscuroPreferido' manualmente aquí,
                    // ya que el Flow 'isDarkTheme' emitirá el nuevo valor y
                    // 'collectAsState' lo actualizará automáticamente, provocando la recomposición.
                }
            )
        }
    }
}