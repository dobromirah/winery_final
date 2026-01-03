package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.*
import bg.tu.varna.si.winery.network.api.BottledWinesApi

class BottledWinesRepo(private val api: BottledWinesApi) {

    suspend fun plan(
        batchId: Long,
        preferredBottleTypeId: Long? = null,
        allowedBottleTypeIds: List<Long>? = null
    ): AutoBottlePlanResponseDto {
        return api.plan(AutoBottlePlanRequestDto(batchId, preferredBottleTypeId, allowedBottleTypeIds))
    }

    suspend fun apply(batchId: Long, items: List<BottleApplyRequestDto.Item>): BottleApplyResponseDto {
        return api.apply(BottleApplyRequestDto(batchId, items))
    }
}
