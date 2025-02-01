package io.citmina.themein.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.citmina.themein.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

// 扩展 Context 获取 DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Serializable
data class SerializableAppInfo(
    val packageName: String,
    val appName: String,
    val path: String
)

class AppPreferencesManager(private val context: Context) {
    private val selectedAppsKey = stringPreferencesKey("selected_apps")

    // 同步获取已选择的应用列表
    suspend fun getSelectedApps(): List<SerializableAppInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val preferences = context.dataStore.data.first()
                val appsJson = preferences[selectedAppsKey] ?: "[]"
                Json.decodeFromString(appsJson)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // **保存已选择的应用列表**
    suspend fun updateSelectedApps(apps: List<AppInfo>) {
        withContext(Dispatchers.IO) {
            val serializableApps = apps.map { 
                SerializableAppInfo(
                    packageName = it.packageName,
                    appName = it.appName,
                    path = it.path
                )
            }
            context.dataStore.edit { preferences ->
                preferences[selectedAppsKey] = Json.encodeToString(serializableApps)
            }
        }
    }
}