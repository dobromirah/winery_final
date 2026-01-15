package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class GrapeVarietyResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String?,
    @SerializedName("yieldLitersPerKg") val yieldLitersPerKg: Double,
    @SerializedName("criticalMinKg") val criticalMinKg: Double
)
data class GrapeVarietyCreateDto(
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String?,
    @SerializedName("yieldLitersPerKg") val yieldLitersPerKg: Double,
    @SerializedName("criticalMinKg") val criticalMinKg: Double
)
