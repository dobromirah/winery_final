package bg.tu.varna.si.winery.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("auth")

object TokenStore {
    private val ACCESS_TOKEN = stringPreferencesKey("accessToken")
    private val ID_TOKEN = stringPreferencesKey("idToken")

    suspend fun save(context: Context, accessToken: String, idToken: String?) {
        context.dataStore.edit {
            it[ACCESS_TOKEN] = accessToken
            if (idToken != null) it[ID_TOKEN] = idToken else it.remove(ID_TOKEN)
        }
    }

    suspend fun getAccessToken(context: Context): String? {
        val prefs = context.dataStore.data.first()
        return prefs[ACCESS_TOKEN]
    }

    suspend fun getIdToken(context: Context): String? {
        val prefs = context.dataStore.data.first()
        return prefs[ID_TOKEN]
    }

    suspend fun clear(context: Context) {
        context.dataStore.edit {
            it.remove(ACCESS_TOKEN)
            it.remove(ID_TOKEN)
        }
    }
}
