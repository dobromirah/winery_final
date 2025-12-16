package bg.tu.varna.si.winery.auth

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

object AuthSession {
    var codeVerifier: String? = null
        private set

    fun generatePkce(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        val verifier = Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        codeVerifier = verifier
        return verifier
    }

    fun codeChallengeS256(verifier: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(verifier.toByteArray(Charsets.US_ASCII))
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}
