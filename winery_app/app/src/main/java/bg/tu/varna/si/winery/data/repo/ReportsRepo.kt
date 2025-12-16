package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.BottleStockReportDto
import bg.tu.varna.si.winery.dto.BottledWineReportDto
import bg.tu.varna.si.winery.dto.GrapeStockReportDto
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import bg.tu.varna.si.winery.network.api.ReportsApi

class ReportsRepo(
    private val api: ReportsApi
) {
    suspend fun grapeStock(): List<GrapeStockReportDto> = api.grapeStock()
    suspend fun bottleStock(): List<BottleStockReportDto> = api.bottleStock()
    suspend fun bottledWine(): List<BottledWineReportDto> = api.bottledWine()
    suspend fun batches(from: String?, to: String?): List<WineBatchResponseDto> = api.batches(from, to)
}
