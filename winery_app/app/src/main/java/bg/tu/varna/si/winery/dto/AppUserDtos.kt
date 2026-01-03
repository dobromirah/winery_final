package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class AppUserCreateDto(
    @SerializedName("keycloakId") val keycloakId: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("role") val role: String
)

data class AppUserResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("keycloakId") val keycloakId: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("role") val role: String
)