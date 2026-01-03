package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.GrapeVarietyCreateDto
import bg.tu.varna.si.winery.dto.GrapeVarietyResponseDto
import bg.tu.varna.si.winery.network.api.GrapeVarietiesApi

class GrapeVarietiesRepo(private val api: GrapeVarietiesApi) {
    suspend fun listAll(): List<GrapeVarietyResponseDto> = api.listAll()
    suspend fun create(dto: GrapeVarietyCreateDto): GrapeVarietyResponseDto = api.create(dto)
}
