package bg.tu.varna.si.winery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import bg.tu.varna.si.winery.auth.AuthConfig
import bg.tu.varna.si.winery.auth.AuthRetrofit
import bg.tu.varna.si.winery.auth.AuthSession
import bg.tu.varna.si.winery.auth.TokenStore
import bg.tu.varna.si.winery.data.repo.NotificationsRepo
import bg.tu.varna.si.winery.data.repo.ReportsRepo
import bg.tu.varna.si.winery.data.repo.WarehouseMovementRepo
import bg.tu.varna.si.winery.data.repo.WarehouseRepo
import bg.tu.varna.si.winery.network.ApiClient
import bg.tu.varna.si.winery.network.api.BottleApi
import bg.tu.varna.si.winery.network.api.GrapeApi
import bg.tu.varna.si.winery.network.api.NotificationsApi
import bg.tu.varna.si.winery.network.api.ReportsApi
import bg.tu.varna.si.winery.ui.common.AppScaffold
import bg.tu.varna.si.winery.ui.home.HomeScreen
import bg.tu.varna.si.winery.ui.login.LoginScreen
import bg.tu.varna.si.winery.ui.notifications.NotificationsScreen
import bg.tu.varna.si.winery.ui.notifications.NotificationsViewModel
import bg.tu.varna.si.winery.ui.reports.ReportsScreen
import bg.tu.varna.si.winery.ui.reports.ReportsViewModel
import bg.tu.varna.si.winery.ui.session.SessionViewModel
import bg.tu.varna.si.winery.ui.warehouse.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var navControllerRef: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            navControllerRef = navController

            val retrofit = remember { ApiClient.create(this@MainActivity) }

            // APIs
            val notificationsApi = remember { retrofit.create(NotificationsApi::class.java) }
            val grapeApi = remember { retrofit.create(GrapeApi::class.java) }
            val bottleApi = remember { retrofit.create(BottleApi::class.java) }
            val reportsApi = remember { retrofit.create(ReportsApi::class.java) }
            val batchesApi = remember { retrofit.create(bg.tu.varna.si.winery.network.api.WineBatchesApi::class.java) }
            val wineTypesApi = remember { retrofit.create(bg.tu.varna.si.winery.network.api.WineTypesApi::class.java) }



            // Repos
            val notificationsRepo = remember { NotificationsRepo(notificationsApi) }
            val movementRepo = remember { WarehouseMovementRepo(grapeApi, bottleApi) }
            val warehouseRepo = remember { WarehouseRepo(reportsApi) }
            val reportsRepo = remember { ReportsRepo(reportsApi) }
            val batchesRepo = remember { bg.tu.varna.si.winery.data.repo.WineBatchesRepo(batchesApi) }
            val wineTypesRepo = remember { bg.tu.varna.si.winery.data.repo.WineTypesRepo(wineTypesApi) }


            // ViewModels (simple remember)
            val notificationsVm = remember { NotificationsViewModel(notificationsRepo) }
            val grapeVm = remember { GrapeMovementViewModel(movementRepo) }
            val bottleVm = remember { BottleMovementViewModel(movementRepo) }
            val stockVm = remember { WarehouseStockViewModel(warehouseRepo) }
            val reportsVm = remember { ReportsViewModel(reportsRepo) }
            val batchesListVm = remember { bg.tu.varna.si.winery.ui.batches.WineBatchesListViewModel(batchesRepo) }
            val batchDetailsVm = remember { bg.tu.varna.si.winery.ui.batches.WineBatchDetailsViewModel(batchesRepo) }
            val createBatchVm = remember { bg.tu.varna.si.winery.ui.batches.CreateWineBatchViewModel(batchesRepo, wineTypesRepo) }

            // Session VM: roles + unreadCount
            val sessionVm = remember { SessionViewModel(this@MainActivity, notificationsRepo) }
            val roles by sessionVm.roles.collectAsState()
            val unreadCount by sessionVm.unreadCount.collectAsState()

            val onLogout: () -> Unit = {
                lifecycleScope.launch {
                    val idToken = TokenStore.getIdToken(this@MainActivity)

                    // local logout
                    TokenStore.clear(this@MainActivity)
                    Log.d("AUTH", "✅ Local tokens cleared")

                    // sso logout
                    openKeycloakLogout(idToken)

                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
                Unit
            }

            // Decide start screen
            LaunchedEffect(Unit) {
                val hasToken = !TokenStore.getAccessToken(this@MainActivity).isNullOrBlank()
                navController.navigate(if (hasToken) "home" else "login") {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
                if (hasToken) sessionVm.refreshAll()
            }

            // Global forced logout (refresh failed, 401 etc.)
            LaunchedEffect(Unit) {
                bg.tu.varna.si.winery.auth.AuthEvents.logout.collect {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            NavHost(navController = navController, startDestination = "login") {

                composable("login") {
                    // LoginScreen не трябва да има Logout, но ти вече си го сложил — ок
                    LoginScreen(onLogout = onLogout)
                }

                composable("home") {
                    // refresh roles + badge on entering home
                    LaunchedEffect(Unit) { sessionVm.refreshAll() }

                    AppScaffold(
                        title = "Home",
                        showBack = false,
                        onBack = { },
                        onLogout = onLogout
                    ) { padding ->
                        HomeScreen(
                            roles = roles,
                            unreadCount = unreadCount,
                            onOpenReports = { navController.navigate("reports") },
                            onOpenWarehouseStock = { navController.navigate("warehouse-stock") },
                            onOpenNotifications = { navController.navigate("notifications") },
                            onOpenGrapeMovement = { navController.navigate("grape-movement") },
                            onOpenBottleMovement = { navController.navigate("bottle-movement") },
                            onOpenBatches = { navController.navigate("batches") }
                        )
                    }
                }

                composable("reports") {
                    AppScaffold(
                        title = "Reports",
                        showBack = true,
                        onBack = { navController.popBackStack() },
                        onLogout = onLogout
                    ) { padding ->
                        ReportsScreen(vm = reportsVm, contentPadding = padding)
                    }
                }

                composable("warehouse-stock") {
                    AppScaffold(
                        title = "Warehouse Stock",
                        showBack = true,
                        onBack = { navController.popBackStack() },
                        onLogout = onLogout
                    ) { padding ->
                        WarehouseStockScreen(vm = stockVm, contentPadding = padding)
                    }
                }

                composable("notifications") {
                    // refresh badge when coming back
                    LaunchedEffect(Unit) { sessionVm.refreshUnreadCount() }

                    AppScaffold(
                        title = "Notifications",
                        showBack = true,
                        onBack = { navController.popBackStack() },
                        onLogout = onLogout
                    ) { padding ->
                        NotificationsScreen(
                            vm = notificationsVm,
                            contentPadding = padding,
                            onMarkedRead = { sessionVm.refreshUnreadCount() }
                        )
                    }
                }

                composable("grape-movement") {
                    AppScaffold(
                        title = "Grape Stock IN/OUT",
                        showBack = true,
                        onBack = { navController.popBackStack() },
                        onLogout = onLogout
                    ) { padding ->
                        GrapeMovementScreen(vm = grapeVm, contentPadding = padding)
                    }
                }

                composable("bottle-movement") {
                    AppScaffold(
                        title = "Bottle Stock IN/OUT",
                        showBack = true,
                        onBack = { navController.popBackStack() },
                        onLogout = onLogout
                    ) { padding ->
                        BottleMovementScreen(vm = bottleVm, contentPadding = padding)
                    }
                }
                composable("batches") { backStackEntry ->
                    // auto-refresh trigger (set from create screen)
                    LaunchedEffect(Unit) {
                        backStackEntry.savedStateHandle
                            .getStateFlow("batches_refresh", 0L)
                            .collect { tick ->
                                if (tick != 0L) batchesListVm.load()
                            }
                    }

                    val canCreate = roles.contains("OPERATOR")

                    AppScaffold(
                        title = "Wine Batches",
                        showBack = true,
                        onBack = { navController.popBackStack() },
                        onLogout = onLogout
                    ) { padding ->
                        bg.tu.varna.si.winery.ui.batches.WineBatchesListScreen(
                            vm = batchesListVm,
                            contentPadding = padding,
                            canCreate = canCreate,
                            onCreate = { navController.navigate("batch-create") },
                            onOpenDetails = { id -> navController.navigate("batch/$id") }
                        )
                    }
                }

                composable("batch-create") {
                    AppScaffold(
                        title = "Create Batch",
                        showBack = true,
                        onBack = { navController.popBackStack() },
                        onLogout = onLogout
                    ) { padding ->
                        bg.tu.varna.si.winery.ui.batches.CreateWineBatchScreen(
                            vm = createBatchVm,
                            contentPadding = padding,
                            onCreated = { createdId ->
                                // 1) trigger refresh in list
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("batches_refresh", System.currentTimeMillis())

                                // 2) go back to list
                                navController.popBackStack()

                                // (по избор) можеш и да отвориш детайли:
                                // navController.navigate("batch/$createdId")
                            }
                        )
                    }
                }


            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val data = intent.data ?: return
        Log.d("AUTH", "Callback URI: $data")

        val code = data.getQueryParameter("code") ?: return
        val verifier = AuthSession.codeVerifier ?: return

        lifecycleScope.launch {
            try {
                val token = AuthRetrofit.api.exchangeCode(code = code, codeVerifier = verifier)

                TokenStore.save(
                    context = this@MainActivity,
                    accessToken = token.accessToken,
                    idToken = token.idToken,
                    refreshToken = token.refreshToken,
                    expiresInSec = token.expiresIn
                )

                navControllerRef?.navigate("home") {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
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
