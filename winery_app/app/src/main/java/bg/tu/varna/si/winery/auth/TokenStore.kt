package bg.tu.varna.si.winery.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("auth")

object TokenStore {
    private val ACCESS_TOKEN = stringPreferencesKey("accessToken")
    private val ID_TOKEN = stringPreferencesKey("idToken")
    private val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
    private val EXPIRES_AT_MS = longPreferencesKey("expiresAtMs")

    suspend fun save(
        context: Context,
        accessToken: String,
        idToken: String?,
        refreshToken: String?,
        expiresInSec: Long
    ) {
        val expiresAt = System.currentTimeMillis() + (expiresInSec * 1000L) - 10_000L // -10s buffer
        context.dataStore.edit {
            it[ACCESS_TOKEN] = accessToken
            if (idToken != null) it[ID_TOKEN] = idToken else it.remove(ID_TOKEN)
            if (refreshToken != null) it[REFRESH_TOKEN] = refreshToken else it.remove(REFRESH_TOKEN)
            it[EXPIRES_AT_MS] = expiresAt
        }
    }

    suspend fun getAccessToken(context: Context): String? =
        context.dataStore.data.first()[ACCESS_TOKEN]

    suspend fun getIdToken(context: Context): String? =
        context.dataStore.data.first()[ID_TOKEN]

    suspend fun getRefreshToken(context: Context): String? =
        context.dataStore.data.first()[REFRESH_TOKEN]

    suspend fun isExpired(context: Context): Boolean {
        val exp = context.dataStore.data.first()[EXPIRES_AT_MS] ?: 0L
        return System.currentTimeMillis() >= exp
    }

    suspend fun clear(context: Context) {
        context.dataStore.edit {
            it.remove(ACCESS_TOKEN)
            it.remove(ID_TOKEN)
            it.remove(REFRESH_TOKEN)
            it.remove(EXPIRES_AT_MS)
        }
    }
}
