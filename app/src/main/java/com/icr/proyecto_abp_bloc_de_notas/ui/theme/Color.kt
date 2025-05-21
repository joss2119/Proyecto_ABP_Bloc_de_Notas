package com.icr.proyecto_abp_bloc_de_notas.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


// --- Colores para TextField ---

// Colores para Tema CLARO
val LightTextFieldFocusedBackgroundColor = Color(0xFFE8EAF6) // Un lavanda muy suave
val LightTextFieldDefaultBackgroundColor = Color.Transparent // O Color(0xFFF5F5F5)
val LightTextFieldFocusedBorderColor = Purple40 // Tu color primario claro
val LightTextFieldDefaultBorderColor = Color.Gray
val LightTextFieldFocusedLabelColor = Purple40
val LightTextFieldDefaultLabelColor = Color.DarkGray
val LightTextFieldFocusedContentColor = Color.Black
val LightTextFieldDefaultContentColor = Color.DarkGray
val LightTextFieldCursorColor = Purple40
val LightTextFieldSelectionHandleColor = Purple40
val LightTextFieldSelectionBackgroundColor = Purple40.copy(alpha = 0.3f)

// Colores para Tema OSCURO
val DarkTextFieldFocusedBackgroundColor = Color(0xFF2C2C3A) // Un gris azulado oscuro
val DarkTextFieldDefaultBackgroundColor = Color.Transparent // O Color(0xFF22222A)
val DarkTextFieldFocusedBorderColor = Purple80 // Tu color primario oscuro
val DarkTextFieldDefaultBorderColor = Color.DarkGray
val DarkTextFieldFocusedLabelColor = Purple80
val DarkTextFieldDefaultLabelColor = Color.LightGray
val DarkTextFieldFocusedContentColor = Color.White
val DarkTextFieldDefaultContentColor = Color.LightGray
val DarkTextFieldCursorColor = Purple80
val DarkTextFieldSelectionHandleColor = Purple80
val DarkTextFieldSelectionBackgroundColor = Purple80.copy(alpha = 0.4f)


// Para el BasicTextField de búsqueda (que no tiene borde ni label por defecto)
// Estos también podrían necesitar variantes claro/oscuro si los usas con fondos diferentes.
// Por ahora, los mantengo como estaban, pero considera esto.
val SearchFieldFocusedBackgroundColor = Color(0xFFE8EAF6)
val SearchFieldDefaultBackgroundColor = Color.Transparent