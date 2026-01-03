package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.AppUserCreateDto
import bg.tu.varna.si.winery.dto.AppUserResponseDto
import bg.tu.varna.si.winery.network.api.UsersApi

class UsersRepo(private val api: UsersApi) {

    suspend fun listAll(): List<AppUserResponseDto> = api.listAll()

    suspend fun create(keycloakId: String, fullName: String, role: String): AppUserResponseDto {
        return api.create(AppUserCreateDto(keycloakId = keycloakId, fullName = fullName, role = role))
    }
}
