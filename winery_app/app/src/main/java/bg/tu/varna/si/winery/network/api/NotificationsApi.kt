package bg.tu.varna.si.winery.network.api

import bg.tu.varna.si.winery.dto.NotificationDto
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationsApi {

    @GET("notifications/unread")
    suspend fun unread(): List<NotificationDto>

    @PUT("notifications/{id}/read")
    suspend fun markRead(@Path("id") id: Long): NotificationDto
}