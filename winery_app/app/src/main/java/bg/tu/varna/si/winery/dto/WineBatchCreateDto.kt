package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class WineBatchCreateDto(
    @SerializedName("wineTypeId") val wineTypeId: Long,
    @SerializedName("plannedLiters") val plannedLiters: Double
)
