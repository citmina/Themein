package io.citmina.themein.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.citmina.themein.model.AppInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

// 为 Context 类扩展 dataStore 属性
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Serializable
data class SerializableAppInfo(
    val packageName: String,
    val appName: String
)

class AppPreferencesManager(private val context: Context) {
    private val selectedAppsKey = stringPreferencesKey("selected_apps")
    
    // 获取已选择的应用列表
    val selectedAppsFlow: Flow<List<SerializableAppInfo>> = context.dataStore.data
        .map { preferences ->
            val appsJson = preferences[selectedAppsKey] ?: "[]"
            try {
                Json.decodeFromString<List<SerializableAppInfo>>(appsJson)
            } catch (e: Exception) {
                emptyList()
            }
        }
    
    // 保存已选择的应用列表
    suspend fun updateSelectedApps(apps: List<AppInfo>) {
        val serializableApps = apps.map { 
            SerializableAppInfo(
                packageName = it.packageName,
                appName = it.appName
            )
        }
        context.dataStore.edit { preferences ->
            preferences[selectedAppsKey] = Json.encodeToString(serializableApps)
        }
    }
} 