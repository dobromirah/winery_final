package bg.tu.varna.si.winery.ui.batches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.WineBatchesRepo
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class WineBatchesListViewModel(
    private val repo: WineBatchesRepo
) : ViewModel() {

    private val _items = MutableStateFlow<List<WineBatchResponseDto>>(emptyList())
    val items: StateFlow<List<WineBatchResponseDto>> = _items

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

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
}
