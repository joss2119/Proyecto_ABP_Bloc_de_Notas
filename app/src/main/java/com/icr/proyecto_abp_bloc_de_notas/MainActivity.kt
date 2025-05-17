package com.icr.proyecto_abp_bloc_de_notas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) Desactiva el ajuste automático de las Insets:
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 2) Crea un WindowInsetsControllerCompat para cambiar la apariencia de iconos
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        // true → iconos oscuros; false → iconos claros
        insetsController.isAppearanceLightStatusBars = true
        insetsController.isAppearanceLightNavigationBars = true

        setContent {
            // Initialize PantallaPrincipalNotasApp
            // It handles its own theme state internally based on initialDarkTheme
            // and calls onThemeChange when the theme is modified in settings.
            PantallaPrincipalNotasApp(
                // initialDarkTheme = determineInitialDarkTheme(savedThemePreference), // Example
                onThemeChange = { isDark ->
                    // Save the new theme preference if needed
                    // saveThemePreference(isDark)
                }
            )
        }
    }
}