package bg.tu.varna.si.winery.ui.warehouse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.WarehouseRepo
import bg.tu.varna.si.winery.dto.BottleStockReportDto
import bg.tu.varna.si.winery.dto.GrapeStockReportDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WarehouseStockViewModel(
    private val repo: WarehouseRepo
) : ViewModel() {

    private val _grapes = MutableStateFlow<List<GrapeStockReportDto>>(emptyList())
    val grapes: StateFlow<List<GrapeStockReportDto>> = _grapes

    private val _bottles = MutableStateFlow<List<BottleStockReportDto>>(emptyList())
    val bottles: StateFlow<List<BottleStockReportDto>> = _bottles

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _grapes.value = repo.grapeStock()
                _bottles.value = repo.bottleStock()
            } finally {
                _loading.value = false
            }
        }
    }
}
