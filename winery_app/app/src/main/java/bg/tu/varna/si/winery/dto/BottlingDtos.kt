package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class AutoBottlePlanRequestDto(
    @SerializedName("batchId") val batchId: Long,
    @SerializedName("preferredBottleTypeId") val preferredBottleTypeId: Long? = null,
    @SerializedName("allowedBottleTypeIds") val allowedBottleTypeIds: List<Long>? = null
)

data class BottlePlanItemDto(
    @SerializedName("bottleTypeId") val bottleTypeId: Long,
    @SerializedName("volumeMl") val volumeMl: Int,
    @SerializedName("description") val description: String,
    @SerializedName("count") val count: Int
)

data class AutoBottlePlanResponseDto(
    @SerializedName("items") val items: List<BottlePlanItemDto>,
    @SerializedName("leftoverLiters") val leftoverLiters: Double,
    @SerializedName("plannedBottledLiters") val plannedBottledLiters: Double
)

data class BottleApplyRequestDto(
    @SerializedName("batchId") val batchId: Long,
    @SerializedName("items") val items: List<Item>
) {
    data class Item(
        @SerializedName("bottleTypeId") val bottleTypeId: Long,
        @SerializedName("count") val count: Int
    )
}

data class BottleApplyResponseDto(
    @SerializedName("batchId") val batchId: Long,
    @SerializedName("bottledNowLiters") val bottledNowLiters: Double,
    @SerializedName("totalBottledLiters") val totalBottledLiters: Double,
    @SerializedName("leftoverLiters") val leftoverLiters: Double,
    @SerializedName("items") val items: List<BottlePlanItemDto>
)
