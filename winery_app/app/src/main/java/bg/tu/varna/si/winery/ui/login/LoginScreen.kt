package bg.tu.varna.si.winery.ui.login

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.wineryapp.auth.AuthConfig
import androidx.core.net.toUri

@Composable
fun LoginScreen() {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { openKeycloakLogin(context) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Login with Keycloak")
        }
    }
}

private fun openKeycloakLogin(context: Context) {
    val loginUri = AuthConfig.AUTH_ENDPOINT.toUri().buildUpon()
        .appendQueryParameter("client_id", AuthConfig.CLIENT_ID)
        .appendQueryParameter("redirect_uri", AuthConfig.REDIRECT_URI)
        .appendQueryParameter("response_type", "code")
        .appendQueryParameter("scope", "openid")
        .build()

    CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
        .launchUrl(context, loginUri)
}
