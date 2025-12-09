package bg.tu.varna.si.winery.auth

data class TokenResponse(
    val accessToken: String,
    val expiresIn: Int,
    val refreshExpiresIn: Int,
    val refreshToken: String,
    val tokenType: String
)
