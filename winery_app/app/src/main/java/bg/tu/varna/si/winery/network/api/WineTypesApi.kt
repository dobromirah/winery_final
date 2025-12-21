package bg.tu.varna.si.winery.network.api

import bg.tu.varna.si.winery.auth.AuthRetrofit.api
import bg.tu.varna.si.winery.dto.WineBatchProduceDto
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import bg.tu.varna.si.winery.dto.WineTypeDto
import bg.tu.varna.si.winery.dto.WineTypeMaxLitersDto
import retrofit2.http.GET
import retrofit2.http.Path

interface WineTypesApi {
    @GET("wine-types")
    suspend fun listAll(): List<WineTypeDto>

    @GET("wine-types/{id}/max-planned-liters")
    suspend fun getMaxPlannedLiters(@Path("id") id: Long): WineTypeMaxLitersDto

}
