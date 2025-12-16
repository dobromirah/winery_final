package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class WineTypeDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("color")
    val color: String, // "WHITE" / "RED" (или както е при теб)

    @SerializedName("description")
    val description: String?
)
