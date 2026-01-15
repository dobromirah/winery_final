package bg.tu.varna.si.winery.network

import android.content.Context
import bg.tu.varna.si.winery.auth.AuthEvents
import bg.tu.varna.si.winery.auth.AuthRetrofit
import bg.tu.varna.si.winery.auth.TokenStore
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenRefreshAuthenticator(
    private val context: Context
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // ако вече сме пробвали много пъти – stop
        if (responseCount(response) >= 2) return null

        val refreshToken = runBlocking { TokenStore.getRefreshToken(context) }
        if (refreshToken.isNullOrBlank()) {
            runBlocking { TokenStore.clear(context) }
            AuthEvents.emitLogout()
            return null
        }

        return try {
            val newTokens = runBlocking { AuthRetrofit.api.refreshToken(refreshToken = refreshToken) }

            runBlocking {
                TokenStore.save(
                    context = context,
                    accessToken = newTokens.accessToken,
                    idToken = newTokens.idToken,
                    refreshToken = newTokens.refreshToken ?: refreshToken, // понякога не връща нов refresh
                    expiresInSec = newTokens.expiresIn
                )
            }

            response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokens.accessToken}")
                .build()

        } catch (e: Exception) {
            runBlocking { TokenStore.clear(context) }
            AuthEvents.emitLogout()
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var r: Response? = response
        var count = 1
        while (r?.priorResponse != null) {
            count++
            r = r.priorResponse
        }
        return count
    }
}
