package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class NotificationDto(
    @SerializedName("id") val id: Long,
    @SerializedName("type") val type: String,
    @SerializedName("resourceType") val resourceType: String,
    @SerializedName("resourceId") val resourceId: Long?,
    @SerializedName("level") val level: String,
    @SerializedName("message") val message: String,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("isRead") val isRead: Boolean
)
