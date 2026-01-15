package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class GrapeStockMovementCreateDto(
    @SerializedName("varietyId") val varietyId: Long,
    @SerializedName("quantityKg") val quantityKg: Double,
    @SerializedName("movementType") val movementType: String // "IN" / "OUT"
)

data class BottleStockMovementCreateDto(
    @SerializedName("bottleTypeId") val bottleTypeId: Long,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("movementType") val movementType: String // "IN" / "OUT"
)

data class GrapeStockMovementResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("varietyId") val varietyId: Long,
    @SerializedName("quantityKg") val quantityKg: Double,
    @SerializedName("movementType") val movementType: String,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("createdById") val createdById: Long? = null,
    @SerializedName("notifications") val notifications: List<NotificationDto>? = null
)
data class BottleStockMovementResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("bottleTypeId") val bottleTypeId: Long,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("movementType") val movementType: String,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("createdById") val createdById: Long? = null,
    @SerializedName("notifications") val notifications: List<NotificationDto>? = null
)