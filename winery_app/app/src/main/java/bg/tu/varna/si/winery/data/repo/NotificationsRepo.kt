package bg.tu.varna.si.winery.data.repo

import bg.tu.varna.si.winery.dto.NotificationDto
import bg.tu.varna.si.winery.network.api.NotificationsApi

class NotificationsRepo(
    private val api: NotificationsApi
) {
    suspend fun unread(): List<NotificationDto> = api.unread()
    suspend fun markRead(id: Long): NotificationDto = api.markRead(id)
}
