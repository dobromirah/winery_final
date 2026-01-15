package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class WineTypeMaxLitersDto(
    @SerializedName("wineTypeId") val wineTypeId: Long,
    @SerializedName("maxLiters") val maxLiters: Double,
    @SerializedName("limitingVarietyName") val limitingVarietyName: String?,
    @SerializedName("limits") val limits: List<VarietyLimitDto> = emptyList()
) {
    data class VarietyLimitDto(
        @SerializedName("varietyId") val varietyId: Long,
        @SerializedName("varietyName") val varietyName: String,
        @SerializedName("availableKg") val availableKg: Double,
        @SerializedName("kgPerLiter") val kgPerLiter: Double,
        @SerializedName("maxLitersForVariety") val maxLitersForVariety: Double
    )
}