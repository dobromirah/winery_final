package bg.tu.varna.si.winery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import bg.tu.varna.si.winery.auth.AuthRetrofit
import bg.tu.varna.si.winery.auth.TokenStore
import bg.tu.varna.si.winery.ui.login.LoginScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        intent.data?.getQueryParameter("code")?.let { code ->
            Log.d("AUTH", "Authorization code received")

            lifecycleScope.launch {
                try {
                    val token = AuthRetrofit.api.getToken(code = code)
                    TokenStore.save(this@MainActivity, token.accessToken)
                    Log.d("AUTH", "Token stored successfully ✅")
                } catch (e: Exception) {
                    Log.e("AUTH", "Token exchange failed", e)
                }
            }
        }
    }
}
