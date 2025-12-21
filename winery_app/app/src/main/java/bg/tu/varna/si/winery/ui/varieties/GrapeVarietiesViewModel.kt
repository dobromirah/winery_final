package bg.tu.varna.si.winery.ui.varieties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.GrapeVarietiesRepo
import bg.tu.varna.si.winery.dto.GrapeVarietyCreateDto
import bg.tu.varna.si.winery.dto.GrapeVarietyDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class GrapeVarietiesViewModel(
    private val repo: GrapeVarietiesRepo
) : ViewModel() {

    private val _items = MutableStateFlow<List<GrapeVarietyDto>>(emptyList())
    val items: StateFlow<List<GrapeVarietyDto>> = _items

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

    fun create(name: String, category: String?, yieldLitersPerKg: Double, criticalMinKg: Double) {
        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            try {
                repo.create(
                    GrapeVarietyCreateDto(
                        name = name.trim(),
                        category = category?.trim()?.ifBlank { null },
                        yieldLitersPerKg = yieldLitersPerKg,
                        criticalMinKg = criticalMinKg
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
