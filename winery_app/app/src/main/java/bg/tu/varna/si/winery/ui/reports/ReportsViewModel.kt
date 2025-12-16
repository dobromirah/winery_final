package bg.tu.varna.si.winery.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.ReportsRepo
import bg.tu.varna.si.winery.dto.BottleStockReportDto
import bg.tu.varna.si.winery.dto.BottledWineReportDto
import bg.tu.varna.si.winery.dto.GrapeStockReportDto
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportsViewModel(
    private val repo: ReportsRepo
) : ViewModel() {

    private val _grapes = MutableStateFlow<List<GrapeStockReportDto>>(emptyList())
    val grapes: StateFlow<List<GrapeStockReportDto>> = _grapes

    private val _bottles = MutableStateFlow<List<BottleStockReportDto>>(emptyList())
    val bottles: StateFlow<List<BottleStockReportDto>> = _bottles

    private val _bottledWine = MutableStateFlow<List<BottledWineReportDto>>(emptyList())
    val bottledWine: StateFlow<List<BottledWineReportDto>> = _bottledWine

    private val _batches = MutableStateFlow<List<WineBatchResponseDto>>(emptyList())
    val batches: StateFlow<List<WineBatchResponseDto>> = _batches

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAllStock() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _grapes.value = repo.grapeStock()
                _bottles.value = repo.bottleStock()
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadBottledWine() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _bottledWine.value = repo.bottledWine()
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadBatches(from: String?, to: String?) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _batches.value = repo.batches(from, to)
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }
}
