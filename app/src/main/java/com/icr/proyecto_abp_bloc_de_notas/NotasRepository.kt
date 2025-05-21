package com.icr.proyecto_abp_bloc_de_notas // O tu paquete de datos/repositorio

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

// Define el DataStore para las notas (nombre diferente al de user_preferences)
private val Context.notasDataStore: DataStore<Preferences> by preferencesDataStore(name = "notas_store")

class NotasRepository(private val context: Context) {

    private object PreferencesKeys {
        val LISTA_NOTAS_JSON = stringPreferencesKey("lista_notas_json_v2") // Cambié el nombre por si había datos antiguos con otra estructura
        val CONTADOR_ID_NOTAS = intPreferencesKey("contador_id_notas_v2")
    }

    val listaNotasFlow: Flow<List<Nota>> = context.notasDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("NotasRepo", "Error leyendo preferencias de notas", exception)
                emit(emptyPreferences()) // Emitir preferencias vacías para que el map no falle
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val jsonString = preferences[PreferencesKeys.LISTA_NOTAS_JSON]
            Log.d("NotasRepo", "Leyendo JSON de notas: $jsonString")
            if (!jsonString.isNullOrEmpty()) {
                try {
                    val notas = Json.decodeFromString<List<Nota>>(jsonString)
                    Log.d("NotasRepo", "Notas deserializadas correctamente: ${notas.size} notas")
                    notas
                } catch (e: Exception) {
                    Log.e("NotasRepo", "ERROR deserializando notas", e)
                    emptyList<Nota>()
                }
            } else {
                Log.d("NotasRepo", "No se encontró JSON de notas, devolviendo lista vacía.")
                emptyList<Nota>()
            }
        }

    suspend fun guardarNotas(notas: List<Nota>) {
        Log.d("NotasRepo", "Intentando guardar ${notas.size} notas.")
        try {
            val jsonString = Json.encodeToString(notas)
            Log.d("NotasRepo", "JSON a guardar para notas: $jsonString")
            context.notasDataStore.edit { preferences ->
                preferences[PreferencesKeys.LISTA_NOTAS_JSON] = jsonString
            }
            Log.i("NotasRepo", "Notas GUARDADAS exitosamente en DataStore.")
        } catch (e: Exception) {
            Log.e("NotasRepo", "ERROR guardando notas", e)
        }
    }

    val contadorIdNotasFlow: Flow<Int> = context.notasDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("NotasRepo", "Error leyendo contador de IDs", exception)
                emit(emptyPreferences()) // Emitir preferencias vacías
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val contador = preferences[PreferencesKeys.CONTADOR_ID_NOTAS] ?: 0
            Log.d("NotasRepo", "Leyendo contador de IDs: $contador")
            contador
        }

    suspend fun guardarContadorIdNotas(contador: Int) {
        Log.d("NotasRepo", "Intentando guardar contador de IDs: $contador")
        try {
            context.notasDataStore.edit { preferences ->
                preferences[PreferencesKeys.CONTADOR_ID_NOTAS] = contador
            }
            Log.i("NotasRepo", "Contador de IDs GUARDADO exitosamente: $contador")
        } catch (e: Exception) {
            Log.e("NotasRepo", "ERROR guardando contador de IDs", e)
        }
    }
}