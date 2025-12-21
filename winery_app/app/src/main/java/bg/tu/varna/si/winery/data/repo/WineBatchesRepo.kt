package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.WineBatchProduceDto
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import bg.tu.varna.si.winery.network.api.WineBatchesApi

class WineBatchesRepo(
    private val api: WineBatchesApi
) {
    suspend fun listAll(): List<WineBatchResponseDto> = api.listAll()
    suspend fun getById(id: Long): WineBatchResponseDto = api.getById(id)

    suspend fun create(dto: bg.tu.varna.si.winery.dto.WineBatchCreateDto): WineBatchResponseDto =
        api.create(dto)

    suspend fun setProduced(id: Long, producedLiters: Double): WineBatchResponseDto {
        return api.setProduced(id, WineBatchProduceDto(producedLiters))
    }

    suspend fun cancel(id: Long) = api.cancel(id)
}
