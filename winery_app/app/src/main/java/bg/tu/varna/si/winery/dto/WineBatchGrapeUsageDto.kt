package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class WineBatchGrapeUsageDto(
    @SerializedName("grapeVarietyId") val grapeVarietyId: Long,
    @SerializedName("grapeVarietyName") val grapeVarietyName: String,
    @SerializedName("kgUsed") val kgUsed: Double
)


