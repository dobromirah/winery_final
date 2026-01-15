package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.*
import bg.tu.varna.si.winery.network.api.BottleApi
import bg.tu.varna.si.winery.network.api.GrapeApi

class WarehouseMovementRepo(
    private val grapeApi: GrapeApi,
    private val bottleApi: BottleApi
) {
    suspend fun grapeVarieties(): List<GrapeVarietyDto> = grapeApi.varieties()
    suspend fun bottleTypes(): List<BottleTypeDto> = bottleApi.bottleTypes()

    suspend fun createGrapeMovement(dto: GrapeStockMovementCreateDto): GrapeStockMovementResponseDto =
        grapeApi.createMovement(dto)

    suspend fun createBottleMovement(dto: BottleStockMovementCreateDto): BottleStockMovementResponseDto =
        bottleApi.createMovement(dto)
}
