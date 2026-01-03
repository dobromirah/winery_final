package bg.tu.varna.si.winery.ui.winetypes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.WineTypesRepo
import bg.tu.varna.si.winery.dto.WineTypeCreateDto
import bg.tu.varna.si.winery.dto.WineTypeDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class WineTypesAdminViewModel(
    private val repo: WineTypesRepo
) : ViewModel() {

    private val _items = MutableStateFlow<List<WineTypeDto>>(emptyList())
    val items: StateFlow<List<WineTypeDto>> = _items

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
                _error.value = "HTTP ${e.code()} (${e.message()})"
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun create(name: String, color: String, description: String?) {
        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            try {
                repo.create(
                    WineTypeCreateDto(
                        name = name.trim(),
                        color = color.trim(),
                        description = description?.trim()?.ifBlank { null }
                    )
                )
                load()
            } catch (e: HttpException) {
                _error.value = "HTTP ${e.code()} (${e.message()})"
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _saving.value = false
            }
        }
    }
}
