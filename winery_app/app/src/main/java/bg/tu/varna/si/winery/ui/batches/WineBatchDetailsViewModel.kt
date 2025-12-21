package bg.tu.varna.si.winery.ui.batches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.WineBatchesRepo
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class WineBatchDetailsViewModel(
    private val repo: WineBatchesRepo
) : ViewModel() {

    private val _item = MutableStateFlow<WineBatchResponseDto?>(null)
    val item: StateFlow<WineBatchResponseDto?> = _item

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load(id: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _item.value = repo.getById(id)
            } catch (e: HttpException) {
                _error.value = "HTTP ${e.code()} (${e.message()})"
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }
    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving

    fun setProduced(id: Long, producedLiters: Double) {
        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            try {
                _item.value = repo.setProduced(id, producedLiters)
            } catch (e: HttpException) {
                _error.value = "HTTP ${e.code()} (${e.message()})"
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _saving.value = false
            }
        }
    }

    fun cancel(id: Long) {
        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            try {
                _item.value = repo.cancel(id)
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
