package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.WineTypeCreateDto
import bg.tu.varna.si.winery.dto.WineTypeDto
import bg.tu.varna.si.winery.network.api.WineTypesApi

class WineTypesRepo(
    private val api: WineTypesApi
) {
    suspend fun listAll(): List<WineTypeDto> = api.listAll()

    suspend fun getMaxPlannedLiters(id: Long) = api.getMaxPlannedLiters(id)
    suspend fun create(dto: WineTypeCreateDto): WineTypeDto = api.create(dto)

}
