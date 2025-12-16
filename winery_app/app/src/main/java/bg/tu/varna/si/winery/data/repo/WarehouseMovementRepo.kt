package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.BottleStockMovementCreateDto
import bg.tu.varna.si.winery.dto.BottleTypeDto
import bg.tu.varna.si.winery.dto.GrapeStockMovementCreateDto
import bg.tu.varna.si.winery.dto.GrapeVarietyDto
import bg.tu.varna.si.winery.network.api.BottleApi
import bg.tu.varna.si.winery.network.api.GrapeApi

class WarehouseMovementRepo(
    private val grapeApi: GrapeApi,
    private val bottleApi: BottleApi
) {
    suspend fun grapeVarieties(): List<GrapeVarietyDto> = grapeApi.varieties()
    suspend fun bottleTypes(): List<BottleTypeDto> = bottleApi.bottleTypes()

    suspend fun createGrapeMovement(dto: GrapeStockMovementCreateDto) = grapeApi.createMovement(dto)
    suspend fun createBottleMovement(dto: BottleStockMovementCreateDto) = bottleApi.createMovement(dto)
}
