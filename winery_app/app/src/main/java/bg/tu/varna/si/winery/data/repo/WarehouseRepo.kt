package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.BottleStockReportDto
import bg.tu.varna.si.winery.dto.GrapeStockReportDto
import bg.tu.varna.si.winery.network.api.ReportsApi

class WarehouseRepo(
    private val reportsApi: ReportsApi
) {
    suspend fun grapeStock(): List<GrapeStockReportDto> = reportsApi.grapeStock()
    suspend fun bottleStock(): List<BottleStockReportDto> = reportsApi.bottleStock()
}
