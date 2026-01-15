package bg.tu.varna.si.winery.network
//
//import android.content.Context
//import bg.tu.varna.si.winery.auth.TokenStore
//import kotlinx.coroutines.runBlocking
//import okhttp3.Interceptor
//import okhttp3.Response
//
//class AuthInterceptor(
//    private val context: Context
//) : Interceptor {
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val token = runBlocking { TokenStore.getAccessToken(context) }
//
//        val request = chain.request().newBuilder().apply {
//            if (!token.isNullOrBlank()) {
//                addHeader("Authorization", "Bearer $token")
//            }
//        }.build()
//
//        return chain.proceed(request)
//    }
//}
