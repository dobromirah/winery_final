package bg.tu.varna.si.winery.network.api

import bg.tu.varna.si.winery.dto.BottleStockReportDto
import bg.tu.varna.si.winery.dto.GrapeStockReportDto
import retrofit2.http.GET

interface ReportsApi {

    @GET("reports/grapes")
    suspend fun grapeStock(): List<GrapeStockReportDto>

    @GET("reports/bottles")
    suspend fun bottleStock(): List<BottleStockReportDto>
}
