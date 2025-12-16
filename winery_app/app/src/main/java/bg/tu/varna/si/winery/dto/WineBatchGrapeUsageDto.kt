package bg.tu.varna.si.winery.dto

data class WineBatchGrapeUsageDto(
    val grapeVarietyId: Long,
    val grapeVarietyName: String,
    val kgUsed: Double
)
