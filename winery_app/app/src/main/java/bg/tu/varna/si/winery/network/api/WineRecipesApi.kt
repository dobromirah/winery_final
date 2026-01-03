package bg.tu.varna.si.winery.network.api

import bg.tu.varna.si.winery.dto.WineRecipeCreateDto
import bg.tu.varna.si.winery.dto.WineRecipeResponseDto
import retrofit2.http.*

interface WineRecipesApi {

    @GET("wine-recipes/wine-type/{wineTypeId}")
    suspend fun byWineType(@Path("wineTypeId") wineTypeId: Long): List<WineRecipeResponseDto>

    @POST("wine-recipes")
    suspend fun create(@Body dto: WineRecipeCreateDto): WineRecipeResponseDto

    @DELETE("wine-recipes/{id}")

    suspend fun delete(@Path("id") id: Long)
}
