package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class GrapeVarietyDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String
)

data class BottleTypeDto(
    @SerializedName("id") val id: Long,
    @SerializedName("volumeMl") val volumeMl: Int,
    @SerializedName("description") val description: String
)
