package bg.tu.varna.si.winery.ui.users

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.UsersRepo
import bg.tu.varna.si.winery.dto.AppUserResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UsersAdminViewModel(private val repo: UsersRepo) : ViewModel() {

    private val _items = MutableStateFlow<List<AppUserResponseDto>>(emptyList())
    val items: StateFlow<List<AppUserResponseDto>> = _items

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _items.value = repo.listAll()
            } catch (e: HttpException) {
                val body = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
                Log.e("USERS", "LIST HTTP ${e.code()} body=$body", e)
                _error.value = "HTTP ${e.code()} ${body ?: ""}".trim()
            } catch (e: Exception) {
                Log.e("USERS", "LIST ERR", e)
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun create(keycloakId: String, fullName: String, role: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            try {
                repo.create(keycloakId = keycloakId, fullName = fullName, role = role)
                load()
                onSuccess()
            } catch (e: HttpException) {
                val body = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
                Log.e("USERS", "CREATE HTTP ${e.code()} body=$body", e)
                _error.value = "HTTP ${e.code()} ${body ?: ""}".trim()
            } catch (e: Exception) {
                Log.e("USERS", "CREATE ERR", e)
                _error.value = e.message ?: "Unknown error"
            } finally {
                _saving.value = false
            }
        }
    }
}
