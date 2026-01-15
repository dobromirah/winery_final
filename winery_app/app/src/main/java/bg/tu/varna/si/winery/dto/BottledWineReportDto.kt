package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class BottledWineReportDto(
    @SerializedName("id") val id: Long,
    @SerializedName("batchId") val batchId: Long,
    @SerializedName("wineTypeName") val wineTypeName: String,
    @SerializedName("bottleTypeId") val bottleTypeId: Long,
    @SerializedName("volumeMl") val volumeMl: Int,
    @SerializedName("bottleDescription") val bottleDescription: String,
    @SerializedName("quantityBottles") val quantityBottles: Int
)

