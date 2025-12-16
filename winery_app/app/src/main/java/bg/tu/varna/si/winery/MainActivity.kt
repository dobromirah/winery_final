package bg.tu.varna.si.winery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.*
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import bg.tu.varna.si.winery.auth.AuthConfig
import bg.tu.varna.si.winery.auth.AuthRetrofit
import bg.tu.varna.si.winery.auth.AuthSession
import bg.tu.varna.si.winery.auth.TokenStore
import bg.tu.varna.si.winery.data.repo.NotificationsRepo
import bg.tu.varna.si.winery.data.repo.WarehouseMovementRepo
import bg.tu.varna.si.winery.network.ApiClient
import bg.tu.varna.si.winery.network.api.BottleApi
import bg.tu.varna.si.winery.network.api.GrapeApi
import bg.tu.varna.si.winery.network.api.NotificationsApi
import bg.tu.varna.si.winery.network.api.ReportsApi
import bg.tu.varna.si.winery.ui.login.LoginScreen
import bg.tu.varna.si.winery.ui.notifications.NotificationsViewModel
import bg.tu.varna.si.winery.ui.warehouse.BottleMovementViewModel
import bg.tu.varna.si.winery.ui.warehouse.GrapeMovementScreen
import bg.tu.varna.si.winery.ui.warehouse.GrapeMovementViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Retrofit към Quarkus + Bearer token interceptor (ApiClient трябва да е настроен правилно)
            val retrofit = remember { ApiClient.create(this) }

            // APIs
            val notificationsApi = remember { retrofit.create(NotificationsApi::class.java) }
            val reportsApi = remember { retrofit.create(ReportsApi::class.java) } // (ако ти трябва по-късно)
            val grapeApi = remember { retrofit.create(GrapeApi::class.java) }
            val bottleApi = remember { retrofit.create(BottleApi::class.java) }

            // Repos
            val notifRepo = remember { NotificationsRepo(notificationsApi) }
            val movementRepo = remember { WarehouseMovementRepo(grapeApi, bottleApi) }

            // ViewModels (временно без DI)
            val notifVm = remember { NotificationsViewModel(notifRepo) }
            val grapeVm = remember { GrapeMovementViewModel(movementRepo) }
            val bottleVm = remember { BottleMovementViewModel(movementRepo) }

            var hasToken by remember { mutableStateOf(false) }

            // Проверяваме токена при старт / след recreate()
            LaunchedEffect(Unit) {
                hasToken = !TokenStore.getAccessToken(this@MainActivity).isNullOrBlank()
            }

            // ---------- LOGOUT callback ----------
            val onLogout: () -> Unit = {
                lifecycleScope.launch {
                    val idToken = TokenStore.getIdToken(this@MainActivity)

                    // 1) local logout
                    TokenStore.clear(this@MainActivity)
                    Log.d("AUTH", "✅ Local tokens cleared")

                    // 2) Keycloak logout (SSO)
                    openKeycloakLogout(idToken)

                    // 3) refresh UI -> обратно към Login
                    recreate()
                }
            }

            if (!hasToken) {
                // LOGIN UI
                LoginScreen(
                    onLogout = onLogout // ако имаш бутон logout и там, иначе може да го махнеш
                )
            } else {
                // ==========================
                // ✅ Избираш кой екран да тестваш
                // ==========================

//                // 1) Notifications + Logout
//                NotificationsScreen(
//                    vm = notifVm,
//                    onLogout = onLogout
//                )

                // 2) Warehouse - Grape movement
                 GrapeMovementScreen(
                     vm = grapeVm,
                     onLogout = onLogout
                 )

                // 3) Warehouse - Bottle movement
                // BottleMovementScreen(vm = bottleVm)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val data = intent.data ?: return
        Log.d("AUTH", "Callback URI: $data")

        // ако се върнеш от logout, може да няма code – игнорирай
        val code = data.getQueryParameter("code") ?: return
        val verifier = AuthSession.codeVerifier ?: return

        lifecycleScope.launch {
            try {
                val token = AuthRetrofit.api.getToken(
                    code = code,
                    codeVerifier = verifier
                )

                // записваме access + id token
                TokenStore.save(this@MainActivity, token.accessToken, token.idToken)

                Log.d("AUTH", "✅ Token stored successfully")

                // refresh UI: Login -> App
                recreate()
            } catch (e: Exception) {
                Log.e("AUTH", "❌ Token exchange failed", e)
            }
        }
    }

    private fun openKeycloakLogout(idToken: String?) {
        val uriBuilder = AuthConfig.LOGOUT_ENDPOINT.toUri().buildUpon()
            .appendQueryParameter("post_logout_redirect_uri", AuthConfig.REDIRECT_URI)

        if (!idToken.isNullOrBlank()) {
            uriBuilder.appendQueryParameter("id_token_hint", idToken)
        }

        val logoutUri = uriBuilder.build()
        Log.d("AUTH", "LOGOUT URL = $logoutUri")

        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
            .launchUrl(this, logoutUri)
    }
}
