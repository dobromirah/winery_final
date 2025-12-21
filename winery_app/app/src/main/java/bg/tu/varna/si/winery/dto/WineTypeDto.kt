package bg.tu.varna.si.winery.dto

import com.google.gson.annotations.SerializedName

data class WineTypeDto(
    @SerializedName(value = "id", alternate = ["wineTypeId", "typeId", "wine_type_id"])
    val id: Long?,

    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String,
    @SerializedName("description") val description: String?
)

