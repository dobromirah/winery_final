package bg.tu.varna.si.winery.network.api

import bg.tu.varna.si.winery.dto.GrapeVarietyCreateDto
import bg.tu.varna.si.winery.dto.GrapeVarietyResponseDto
import bg.tu.varna.si.winery.dto.GrapeVarietyDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GrapeVarietiesApi {
    @GET("grape-varieties")
    suspend fun listAll(): List<GrapeVarietyResponseDto>

    @POST("grape-varieties")
    suspend fun create(@Body dto: GrapeVarietyCreateDto): GrapeVarietyResponseDto
}
