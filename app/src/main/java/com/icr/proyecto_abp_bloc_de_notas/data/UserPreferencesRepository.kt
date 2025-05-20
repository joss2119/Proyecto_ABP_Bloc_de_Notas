package com.icr.proyecto_abp_bloc_de_notas.data // O el paquete que hayas creado

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Extensión para crear la instancia de DataStore a nivel de Context
// El nombre "user_preferences" será el nombre del archivo donde se guardarán.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    // Clave para la preferencia del tema oscuro
    private object PreferencesKeys {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    }

    // Flow para leer la preferencia del tema oscuro
    // Emite 'true' si el tema oscuro está activo, 'false' en caso contrario (o por defecto).
    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            // IOException se lanza si hay un error al leer los datos.
            if (exception is IOException) {
                emit(emptyPreferences()) // Si hay error, emitir preferencias vacías.
            } else {
                throw exception // Re-lanzar otras excepciones.
            }
        }
        .map { preferences ->
            // Leer el valor booleano. Si no existe, por defecto será 'false' (tema claro).
            preferences[PreferencesKeys.IS_DARK_THEME] ?: false
        }

    // Función para guardar la preferencia del tema oscuro
    suspend fun updateDarkThemePreference(isDarkTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDarkTheme
        }
    }
}