package bg.tu.varna.si.winery.network.api

import bg.tu.varna.si.winery.dto.WineBatchCreateDto
import bg.tu.varna.si.winery.dto.WineBatchProduceDto
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface WineBatchesApi {

    @GET("wine-batches")
    suspend fun listAll(): List<WineBatchResponseDto>

    @GET("wine-batches/{id}")
    suspend fun getById(@Path("id") id: Long): WineBatchResponseDto

    @POST("wine-batches")
    suspend fun create(@Body dto: WineBatchCreateDto): WineBatchResponseDto

    @PUT("wine-batches/{id}/produce")
    suspend fun setProduced(
        @Path("id") id: Long,
        @Body dto: WineBatchProduceDto
    ): WineBatchResponseDto

    @POST("wine-batches/{id}/cancel")
    suspend fun cancel(@Path("id") id: Long): WineBatchResponseDto
}
