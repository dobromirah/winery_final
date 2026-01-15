package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class WineRecipeCreateDto(
    @SerializedName("wineTypeId") val wineTypeId: Long,
    @SerializedName("grapeVarietyId") val grapeVarietyId: Long,
    @SerializedName("kgPerLiter") val kgPerLiter: Double
)

data class WineRecipeResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("wineTypeId") val wineTypeId: Long,
    @SerializedName("wineTypeName") val wineTypeName: String,
    @SerializedName("grapeVarietyId") val grapeVarietyId: Long,
    @SerializedName("grapeVarietyName") val grapeVarietyName: String,
    @SerializedName("kgPerLiter") val kgPerLiter: Double
)
