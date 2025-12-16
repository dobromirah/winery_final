package bg.tu.varna.si.winery.ui.login

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import bg.tu.varna.si.winery.auth.AuthConfig
import bg.tu.varna.si.winery.auth.AuthSession

@Composable
fun LoginScreen(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Button(
                onClick = { openKeycloakLogin(context) },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Login with Keycloak")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Logout (clear tokens)")
            }
        }
    }
}

private fun openKeycloakLogin(context: Context) {
    val activity = context as? Activity ?: run {
        Log.e("AUTH", "Context is not Activity - cannot launch CustomTabs")
        return
    }

    // ✅ PKCE
    val verifier = AuthSession.generatePkce()
    val challenge = AuthSession.codeChallengeS256(verifier)

    val loginUri = AuthConfig.AUTH_ENDPOINT.toUri().buildUpon()
        .appendQueryParameter("client_id", AuthConfig.CLIENT_ID)
        .appendQueryParameter("redirect_uri", AuthConfig.REDIRECT_URI)
        .appendQueryParameter("response_type", "code")
        .appendQueryParameter("scope", "openid")
        .appendQueryParameter("code_challenge", challenge)
        .appendQueryParameter("code_challenge_method", "S256")
        .appendQueryParameter("prompt", "login")
        .build()

    Log.d("AUTH", "LOGIN URL = $loginUri")

    CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
        .launchUrl(activity, loginUri)
}
