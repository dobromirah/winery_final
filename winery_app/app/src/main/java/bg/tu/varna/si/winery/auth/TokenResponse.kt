package bg.tu.varna.si.winery.auth

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("refresh_expires_in") val refreshExpiresIn: Long? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("token_type") val tokenType: String? = null,
    @SerializedName("id_token") val idToken: String? = null,
    @SerializedName("scope") val scope: String? = null
)
