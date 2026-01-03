package bg.tu.varna.si.winery.dto

import java.time.LocalDateTime

data class WineBatchResponseDto(
    val id: Long,

    val wineTypeId: Long,
    val wineTypeName: String,

    val plannedLiters: Double,
    val producedLiters: Double,

    val createdAt: String,

    val createdById: Int?,
    val createdByFullName: String?,

    val grapeUsage: List<WineBatchGrapeUsageDto>,
    val status: String

)
