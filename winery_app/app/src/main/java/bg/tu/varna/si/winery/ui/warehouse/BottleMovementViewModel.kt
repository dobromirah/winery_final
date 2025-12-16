package bg.tu.varna.si.winery.ui.warehouse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.WarehouseMovementRepo
import bg.tu.varna.si.winery.dto.BottleStockMovementCreateDto
import bg.tu.varna.si.winery.dto.BottleTypeDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class BottleMovementViewModel(
    private val repo: WarehouseMovementRepo
) : ViewModel() {

    private val _types = MutableStateFlow<List<BottleTypeDto>>(emptyList())
    val types: StateFlow<List<BottleTypeDto>> = _types

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success

    fun loadBottleTypes() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _types.value = repo.bottleTypes()
            } catch (e: HttpException) {
                _error.value = "HTTP ${e.code()} (${e.message()})"
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun submit(bottleTypeId: Long, qty: Int, movementType: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _success.value = null
            try {
                repo.createBottleMovement(
                    BottleStockMovementCreateDto(
                        bottleTypeId = bottleTypeId,
                        quantity = qty,
                        movementType = movementType
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
