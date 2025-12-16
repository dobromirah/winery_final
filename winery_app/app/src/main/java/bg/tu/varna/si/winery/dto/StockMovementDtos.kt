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
