package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.WineRecipeCreateDto
import bg.tu.varna.si.winery.dto.WineRecipeResponseDto
import bg.tu.varna.si.winery.network.api.WineRecipesApi

class WineRecipesRepo(private val api: WineRecipesApi) {
    suspend fun byWineType(wineTypeId: Long): List<WineRecipeResponseDto> = api.byWineType(wineTypeId)
    suspend fun create(dto: WineRecipeCreateDto): WineRecipeResponseDto = api.create(dto)
    suspend fun delete(id: Long) = api.delete(id)
}
