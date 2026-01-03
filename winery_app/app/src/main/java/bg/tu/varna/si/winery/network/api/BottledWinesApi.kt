package bg.tu.varna.si.winery.network.api

import bg.tu.varna.si.winery.dto.*
import retrofit2.http.Body
import retrofit2.http.POST

interface BottledWinesApi {

    @POST("bottled-wines/plan")
    suspend fun plan(@Body dto: AutoBottlePlanRequestDto): AutoBottlePlanResponseDto

    @POST("bottled-wines")
    suspend fun apply(@Body dto: BottleApplyRequestDto): BottleApplyResponseDto
}
