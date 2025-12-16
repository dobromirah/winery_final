package bg.tu.varna.si.winery.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // IMPORTANT: baseUrl is your QUARKUS backend, not Keycloak
    // emulator -> your host machine LAN ip OR 10.0.2.2 if backend is on host and exposed
    private const val BASE_URL = "http://10.0.2.2:8082/"

    fun create(context: Context): Retrofit {
        val okHttp = OkHttpClient.Builder()
            .addInterceptor(AuthHeaderInterceptor(context))
            .authenticator(TokenRefreshAuthenticator(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
