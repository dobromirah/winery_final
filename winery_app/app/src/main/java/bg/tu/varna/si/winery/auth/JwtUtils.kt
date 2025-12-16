package bg.tu.varna.si.winery.auth

import android.util.Base64
import org.json.JSONObject

object JwtUtils {

    fun getRealmRoles(accessToken: String?): Set<String> {
        if (accessToken.isNullOrBlank()) return emptySet()
        return try {
            val parts = accessToken.split(".")
            if (parts.size < 2) return emptySet()

            val payloadJson = String(
                Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            )
            val obj = JSONObject(payloadJson)

            val realmAccess = obj.optJSONObject("realm_access") ?: return emptySet()
            val roles = realmAccess.optJSONArray("roles") ?: return emptySet()

            buildSet {
                for (i in 0 until roles.length()) add(roles.getString(i))
            }
        } catch (_: Exception) {
            emptySet()
        }
    }
}
