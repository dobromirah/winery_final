package bg.tu.varna.si.winery.auth

object AuthConfig {

    // Client ID from Keycloak
    const val CLIENT_ID = "winery-android"

    // Must match Redirect URI in Keycloak
    const val REDIRECT_URI = "winery://callback"

    // CHANGE REALM NAME HERE
    private const val REALM = "winery-realm"

    // Keycloak endpoints
    const val AUTH_ENDPOINT =
        "http://10.0.2.2:8080/realms/$REALM/protocol/openid-connect/auth"

    const val TOKEN_ENDPOINT =
        "http://10.0.2.2:8080/realms/$REALM/protocol/openid-connect/token"
}
