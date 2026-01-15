package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class WineBatchResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("wineTypeId") val wineTypeId: Long,
    @SerializedName("wineTypeName") val wineTypeName: String,
    @SerializedName("plannedLiters") val plannedLiters: Double,
    @SerializedName("producedLiters") val producedLiters: Double,
    @SerializedName("bottledLiters") val bottledLiters: Double,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("createdById") val createdById: Int?,
    @SerializedName("createdByFullName") val createdByFullName: String?,
    @SerializedName("grapeUsage") val grapeUsage: List<WineBatchGrapeUsageDto>,
    @SerializedName("status") val status: String,
    @SerializedName("notifications") val notifications: List<NotificationDto>?

)
