package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.WineBatchCreateDto
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import bg.tu.varna.si.winery.network.api.WineBatchesApi

class WineBatchesRepo(
    private val api: WineBatchesApi
) {
    suspend fun listAll(): List<WineBatchResponseDto> = api.listAll()
    suspend fun getById(id: Long): WineBatchResponseDto = api.getById(id)
    suspend fun create(dto: WineBatchCreateDto): WineBatchResponseDto = api.create(dto)
}
