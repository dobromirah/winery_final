package bg.tu.varna.si.winery.auth

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {

    @FormUrlEncoded
    @POST("realms/winery-realm/protocol/openid-connect/token")

    suspend fun getToken(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("client_id") clientId: String = AuthConfig.CLIENT_ID,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String = AuthConfig.REDIRECT_URI
    ): TokenResponse
}
