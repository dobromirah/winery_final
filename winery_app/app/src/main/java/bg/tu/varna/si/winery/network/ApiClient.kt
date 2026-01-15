package bg.tu.varna.si.winery.network

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor


object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8082/" // локалхост на емулатор

    fun create(context: Context): Retrofit {
        val logging = HttpLoggingInterceptor { msg -> Log.d("HTTP", msg) }
            .apply { level = HttpLoggingInterceptor.Level.BODY }
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
