package bg.tu.varna.si.winery.network.api

import bg.tu.varna.si.winery.dto.BottleStockReportDto
import bg.tu.varna.si.winery.dto.BottledWineReportDto
import bg.tu.varna.si.winery.dto.GrapeStockReportDto
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportsApi {

    @GET("reports/grapes")
    suspend fun grapeStock(): List<GrapeStockReportDto>

    @GET("reports/bottles")
    suspend fun bottleStock(): List<BottleStockReportDto>

    @GET("reports/bottled-wine")
    suspend fun bottledWine(): List<BottledWineReportDto>

    @GET("reports/batches")
    suspend fun batches(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): List<WineBatchResponseDto>
}
