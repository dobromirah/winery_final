package bg.tu.varna.si.winery.network.api

import bg.tu.varna.si.winery.dto.BottleStockMovementCreateDto
import bg.tu.varna.si.winery.dto.BottleStockMovementResponseDto
import bg.tu.varna.si.winery.dto.BottleTypeDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BottleApi {

    @GET("bottle-types")
    suspend fun bottleTypes(): List<BottleTypeDto>

    @POST("bottle-stock")
    suspend fun createMovement(@Body dto: BottleStockMovementCreateDto): BottleStockMovementResponseDto
}
