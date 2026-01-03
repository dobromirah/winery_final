package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class AutoBottleRequestDto(
    @SerializedName("batchId") val batchId: Long
)

data class AutoBottleResponseDto(
    @SerializedName("items") val items: List<Item>,
    @SerializedName("leftoverLiters") val leftoverLiters: Double
) {
    data class Item(
        @SerializedName("bottleTypeId") val bottleTypeId: Long,
        @SerializedName("description") val description: String,
        @SerializedName("volumeMl") val volumeMl: Int,
        @SerializedName("count") val count: Int
    )
}
