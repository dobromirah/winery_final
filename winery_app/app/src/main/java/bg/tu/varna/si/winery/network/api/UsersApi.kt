package bg.tu.varna.si.winery.network.api

import bg.tu.varna.si.winery.dto.AppUserCreateDto
import bg.tu.varna.si.winery.dto.AppUserResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UsersApi {

    @GET("users")
    suspend fun listAll(): List<AppUserResponseDto>

    @POST("users")
    suspend fun create(@Body dto: AppUserCreateDto): AppUserResponseDto

    @DELETE("users/{id}")
    suspend fun delete(@Path("id") id: Long)
}
