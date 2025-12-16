package bg.tu.varna.si.winery.dto

data class BottledWineReportDto(
    val id: Long,

    val batchId: Long,
    val wineTypeName: String,

    val bottleTypeId: Long,
    val volumeMl: Int,
    val bottleDescription: String,

    val quantityBottles: Int
)
