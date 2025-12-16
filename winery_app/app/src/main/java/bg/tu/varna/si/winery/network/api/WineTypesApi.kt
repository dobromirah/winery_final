package bg.tu.varna.si.winery.network.api

import bg.tu.varna.si.winery.dto.WineTypeDto
import retrofit2.http.GET

interface WineTypesApi {
    @GET("wine-types")
    suspend fun listAll(): List<WineTypeDto>
}
