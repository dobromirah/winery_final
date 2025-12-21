package bg.tu.varna.si.winery.dto

data class WineRecipeCreateDto(
    val wineTypeId: Long,
    val grapeVarietyId: Long,
    val kgPerLiter: Double
)

data class WineRecipeResponseDto(
    val id: Long,
    val wineTypeId: Long,
    val wineTypeName: String,
    val grapeVarietyId: Long,
    val grapeVarietyName: String,
    val kgPerLiter: Double
)
