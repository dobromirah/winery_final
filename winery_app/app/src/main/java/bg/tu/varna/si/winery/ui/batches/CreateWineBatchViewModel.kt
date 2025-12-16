package bg.tu.varna.si.winery.ui.batches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.WineBatchesRepo
import bg.tu.varna.si.winery.data.repo.WineTypesRepo
import bg.tu.varna.si.winery.dto.WineBatchCreateDto
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import bg.tu.varna.si.winery.dto.WineTypeDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CreateWineBatchViewModel(
    private val batchesRepo: WineBatchesRepo,
    private val wineTypesRepo: WineTypesRepo
) : ViewModel() {

    private val _types = MutableStateFlow<List<WineTypeDto>>(emptyList())
    val types: StateFlow<List<WineTypeDto>> = _types

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _created = MutableStateFlow<WineBatchResponseDto?>(null)
    val created: StateFlow<WineBatchResponseDto?> = _created

    fun loadTypes() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _types.value = wineTypesRepo.listAll()
            } catch (e: HttpException) {
                _error.value = "HTTP ${e.code()} (${e.message()})"
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun create(wineTypeId: Long, plannedLiters: Double) {
        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            try {
                val dto = WineBatchCreateDto(wineTypeId = wineTypeId, plannedLiters = plannedLiters)
                _created.value = batchesRepo.create(dto)
            } catch (e: HttpException) {
                _error.value = "HTTP ${e.code()} (${e.message()})"
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _saving.value = false
            }
        }
    }

    fun clearCreated() {
        _created.value = null
    }
}
