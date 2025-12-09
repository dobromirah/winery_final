package bg.tu.varna.si.winery.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("auth")

object TokenStore {

    private val ACCESS_TOKEN = stringPreferencesKey("accessToken")

    suspend fun save(context: Context, token: String) {
        context.dataStore.edit {
            it[ACCESS_TOKEN] = token
        }
    }

    suspend fun get(context: Context): String? {
        val prefs = context.dataStore.data.first()
        return prefs[ACCESS_TOKEN]
    }
}
