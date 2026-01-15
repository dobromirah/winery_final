package bg.tu.varna.si.winery.ui.login

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import bg.tu.varna.si.winery.R
import bg.tu.varna.si.winery.auth.AuthConfig
import bg.tu.varna.si.winery.auth.AuthSession

@Composable
fun LoginScreen(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current

    var showBranding by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        showBranding = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.winery),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )
        AnimatedVisibility(
            visible = showBranding,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp),
            enter = fadeIn(animationSpec = tween(650, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        initialOffsetY = { -it / 3 },
                        animationSpec = tween(650, easing = FastOutSlowInEasing)
                    ),
            exit = fadeOut(animationSpec = tween(250)) +
                    slideOutVertically(animationSpec = tween(250))
        ) {
            Column(modifier = Modifier.offset(x = (-70).dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "MirA Winery",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.65f))
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Estate & Vineyards",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Column(
            modifier = Modifier.offset(x = (-15).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { openKeycloakLogin(context) },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Login with Keycloak")
            }

            // Spacer(Modifier.height(12.dp))
            // OutlinedButton(onClick = onLogout, modifier = Modifier.padding(16.dp)) { Text("Logout") }
        }
    }
}

private fun openKeycloakLogin(context: Context) {
    val activity = context as? Activity ?: run {
        Log.e("AUTH", "Context is not Activity - cannot launch CustomTabs")
        return
    }

    // PKCE
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
