package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.WineBatchCreateDto
import bg.tu.varna.si.winery.dto.WineBatchProduceDto
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import bg.tu.varna.si.winery.network.api.WineBatchesApi
import java.time.LocalDateTime

class WineBatchesRepo(
    private val api: WineBatchesApi
) {
    suspend fun listAll(): List<WineBatchResponseDto> =
        api.listAll()
            .sortedByDescending { LocalDateTime.parse(it.createdAt) }

    suspend fun getById(id: Long): WineBatchResponseDto = api.getById(id)

    suspend fun create(dto: WineBatchCreateDto): WineBatchResponseDto =
        api.create(dto)
    suspend fun setProduced(id: Long, producedLiters: Double): WineBatchResponseDto =
        api.setProduced(id, WineBatchProduceDto(producedLiters))
    suspend fun cancel(id: Long): WineBatchResponseDto =
        api.cancel(id)
}
