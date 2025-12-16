package bg.tu.varna.si.winery.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.NotificationsRepo
import bg.tu.varna.si.winery.dto.NotificationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class NotificationsViewModel(
    private val repo: NotificationsRepo
) : ViewModel() {

    private val _items = MutableStateFlow<List<NotificationDto>>(emptyList())
    val items: StateFlow<List<NotificationDto>> = _items

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadUnread() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _items.value = repo.unread()
            } catch (e: HttpException) {
                _error.value = "HTTP ${e.code()} (${e.message()})"
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun markAsRead(id: Long) {
        viewModelScope.launch {
            try {
                repo.markRead(id)
                _items.value = _items.value.filterNot { it.id == id }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to mark as read"
            }
        }
    }
}
