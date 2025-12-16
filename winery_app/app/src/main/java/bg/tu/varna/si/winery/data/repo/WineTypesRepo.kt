package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.WineTypeDto
import bg.tu.varna.si.winery.network.api.WineTypesApi

class WineTypesRepo(
    private val api: WineTypesApi
) {
    suspend fun listAll(): List<WineTypeDto> = api.listAll()
}
