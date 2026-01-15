package bg.tu.varna.si.winery.network.api

import bg.tu.varna.si.winery.dto.GrapeStockMovementCreateDto
import bg.tu.varna.si.winery.dto.GrapeStockMovementResponseDto
import bg.tu.varna.si.winery.dto.GrapeVarietyDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GrapeApi {

    @GET("grape-varieties")
    suspend fun varieties(): List<GrapeVarietyDto>

    @POST("grape-stock")
    suspend fun createMovement(@Body dto: GrapeStockMovementCreateDto): GrapeStockMovementResponseDto
}
