package com.icr.proyecto_abp_bloc_de_notas

import PantallaPrincipalNotas
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.icr.proyecto_abp_bloc_de_notas.ui.theme.Proyecto_ABP_Bloc_de_NotasTheme

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
            Proyecto_ABP_Bloc_de_NotasTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),  // padding top + bottom automático
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaPrincipalNotas()
                }
            }
        }
    }
}
