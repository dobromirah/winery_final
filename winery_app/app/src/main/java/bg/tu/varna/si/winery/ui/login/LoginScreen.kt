package bg.tu.varna.si.winery.ui.login

import android.content.Context
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import bg.tu.varna.si.winery.auth.AuthConfig
import bg.tu.varna.si.winery.auth.AuthSession
import bg.tu.varna.si.winery.auth.Pkce

@Composable
fun LoginScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { openKeycloakLogin(context) }, modifier = Modifier.padding(8.dp)) {
                Text("Login with Keycloak")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onLogout, modifier = Modifier.padding(8.dp)) {
                Text("Logout")
            }
        }
    }
}

private fun openKeycloakLogin(context: Context) {
    val verifier = Pkce.generateVerifier()
    AuthSession.codeVerifier = verifier
    val challenge = Pkce.challengeS256(verifier)

    val loginUri = AuthConfig.AUTH_ENDPOINT.toUri().buildUpon()
        .appendQueryParameter("client_id", AuthConfig.CLIENT_ID)
        .appendQueryParameter("redirect_uri", AuthConfig.REDIRECT_URI)
        .appendQueryParameter("response_type", "code")
        .appendQueryParameter("scope", "openid")
        .appendQueryParameter("code_challenge", challenge)
        .appendQueryParameter("code_challenge_method", "S256")
        .appendQueryParameter("prompt", "login") // ✅ винаги да иска username/password
        .build()

    Log.d("AUTH", "LOGIN URL = $loginUri")

    CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
        .launchUrl(context, loginUri)
}
