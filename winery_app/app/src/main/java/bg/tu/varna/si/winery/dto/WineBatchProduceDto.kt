package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class WineBatchProduceDto(
    @SerializedName("producedLiters") val producedLiters: Double
)
