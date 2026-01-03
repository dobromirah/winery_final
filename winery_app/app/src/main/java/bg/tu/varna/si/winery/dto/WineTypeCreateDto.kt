package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class WineTypeCreateDto(
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String,
    @SerializedName("description") val description: String?
)
