package bg.tu.varna.si.winery.ui.session

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.auth.JwtUtils
import bg.tu.varna.si.winery.auth.TokenStore
import bg.tu.varna.si.winery.data.repo.NotificationsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SessionViewModel(
    private val context: Context,
    private val notificationsRepo: NotificationsRepo
) : ViewModel() {

    private val _roles = MutableStateFlow<Set<String>>(emptySet())
    val roles: StateFlow<Set<String>> = _roles

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    fun refreshSession() {
        viewModelScope.launch {
            val token = TokenStore.getAccessToken(context)
            _roles.value = JwtUtils.getRealmRoles(token)
        }
    }

    fun refreshUnreadCount() {
        viewModelScope.launch {
            try {
                _unreadCount.value = notificationsRepo.unread().size
            } catch (_: Exception) {
            }
        }
    }

    fun refreshAll() {
        refreshSession()
        refreshUnreadCount()
    }
}
