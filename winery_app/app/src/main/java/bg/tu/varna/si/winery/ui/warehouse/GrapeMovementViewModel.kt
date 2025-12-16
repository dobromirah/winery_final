package bg.tu.varna.si.winery.ui.warehouse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.WarehouseMovementRepo
import bg.tu.varna.si.winery.dto.GrapeStockMovementCreateDto
import bg.tu.varna.si.winery.dto.GrapeVarietyDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class GrapeMovementViewModel(
    private val repo: WarehouseMovementRepo
) : ViewModel() {

    private val _varieties = MutableStateFlow<List<GrapeVarietyDto>>(emptyList())
    val varieties: StateFlow<List<GrapeVarietyDto>> = _varieties

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success

    fun loadVarieties() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _varieties.value = repo.grapeVarieties()
            } catch (e: HttpException) {
                _error.value = "HTTP ${e.code()} (${e.message()})"
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun submit(varietyId: Long, qtyKg: Double, movementType: String, createdById: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _success.value = null
            try {
                repo.createGrapeMovement(
                    GrapeStockMovementCreateDto(
                        varietyId = varietyId,
                        quantityKg = qtyKg,
                        movementType = movementType,
                        createdById = createdById
                    )
                )
                _success.value = "✅ Movement saved"
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
