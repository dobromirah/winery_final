package bg.tu.varna.si.winery.dto

data class WineTypeMaxLitersDto(
    val wineTypeId: Long,
    val maxLiters: Double,
    val limitingVarietyName: String?,
    val limits: List<VarietyLimitDto> = emptyList()
) {
    data class VarietyLimitDto(
        val varietyId: Long,
        val varietyName: String,
        val availableKg: Double,
        val kgPerLiter: Double,
        val maxLitersForVariety: Double
    )
}