package com.icr.proyecto_abp_bloc_de_notas.data // O el paquete que hayas creado

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException

// Extensión para crear la instancia de DataStore a nivel de Context
// El nombre "user_preferences" será el nombre del archivo donde se guardarán.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("UserPrefsRepo", "Error leyendo preferencias", exception) // LOG ERROR
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val isDark = preferences[PreferencesKeys.IS_DARK_THEME] ?: false
            Log.d("UserPrefsRepo", "Preferencia de tema leída: $isDark") // LOG LECTURA
            isDark
        }


    suspend fun updateDarkThemePreference(isDarkTheme: Boolean) {
        Log.d("UserPrefsRepo", "INTENTANDO GUARDAR preferencia de tema: $isDarkTheme") // Log A
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDarkTheme
        }
        Log.d("UserPrefsRepo", "Preferencia supuestamente guardada. Verificando lectura inmediata...") // Log B
        // Leer inmediatamente para verificar
        val valorLeido = context.dataStore.data.firstOrNull()?.get(PreferencesKeys.IS_DARK_THEME)
        Log.d("UserPrefsRepo", "Valor REAL leído inmediatamente tras guardar: $valorLeido") // Log C
    }

}
