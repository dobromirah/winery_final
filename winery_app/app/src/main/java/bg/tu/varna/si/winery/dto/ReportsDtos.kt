package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class GrapeStockReportDto(
    @SerializedName("varietyId") val varietyId: Long,
    @SerializedName("varietyName") val varietyName: String,
    @SerializedName("category") val category: String,
    @SerializedName("currentKg") val currentKg: Double,
    @SerializedName("criticalMinKg") val criticalMinKg: Double,
    @SerializedName("belowMinimum") val belowMinimum: Boolean
)

data class BottleStockReportDto(
    @SerializedName("bottleTypeId") val bottleTypeId: Long,
    @SerializedName("volumeMl") val volumeMl: Int,
    @SerializedName("description") val description: String,
    @SerializedName("currentQty") val currentQty: Int,
    @SerializedName("criticalMinQty") val criticalMinQty: Int,
    @SerializedName("belowMinimum") val belowMinimum: Boolean
)
