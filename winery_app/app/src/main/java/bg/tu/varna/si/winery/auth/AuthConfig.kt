package bg.tu.varna.si.winery.auth

object AuthConfig {
    const val CLIENT_ID = "winery-android"
    const val REDIRECT_URI = "winery://callback"

    private const val REALM = "winery-realm"

    private const val HOST = "192.168.1.8"
    const val AUTH_ENDPOINT = "http://$HOST:8081/realms/$REALM/protocol/openid-connect/auth"
    const val TOKEN_ENDPOINT = "http://$HOST:8081/realms/$REALM/protocol/openid-connect/token"
    const val LOGOUT_ENDPOINT = "http://$HOST:8081/realms/$REALM/protocol/openid-connect/logout"

}
