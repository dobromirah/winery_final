package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.GrapeVarietyCreateDto
import bg.tu.varna.si.winery.dto.GrapeVarietyDto
import bg.tu.varna.si.winery.network.api.GrapeVarietiesApi

class GrapeVarietiesRepo(private val api: GrapeVarietiesApi) {
    suspend fun listAll(): List<GrapeVarietyDto> = api.listAll()
    suspend fun create(dto: GrapeVarietyCreateDto): GrapeVarietyDto = api.create(dto)
}
