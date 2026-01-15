package bg.tu.varna.si.winery.ui.batches

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.WineBatchesRepo
import bg.tu.varna.si.winery.data.repo.WineTypesRepo
import bg.tu.varna.si.winery.dto.NotificationDto
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

    private val _maxLiters = MutableStateFlow<Double?>(null)
    val maxLiters: StateFlow<Double?> = _maxLiters

    private val _limitInfo = MutableStateFlow<String?>(null)
    val limitInfo: StateFlow<String?> = _limitInfo

    fun onWineTypeSelected(wineTypeId: Long) {
        viewModelScope.launch {
            _error.value = null
            _maxLiters.value = null
            _limitInfo.value = null
            try {
                val res = wineTypesRepo.getMaxPlannedLiters(wineTypeId)
                _maxLiters.value = res.maxLiters
                _limitInfo.value = res.limitingVarietyName?.let { "Limited by: $it" }
            } catch (e: HttpException) {
                _error.value = "HTTP ${e.code()} (${e.message()})"
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    fun loadTypes() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _types.value = wineTypesRepo.listAll()
                Log.d(TAG, "Loaded wine types: ${_types.value.size}")
            } catch (e: HttpException) {
                val body = safeErrorBody(e)
                Log.e(TAG, "loadTypes HTTP ${e.code()} body=$body", e)
                _error.value =
                    "Load types failed: HTTP ${e.code()}${body.ifBlank { "" }.let { if (it.isNotBlank()) " — $it" else "" }}"
            } catch (e: Exception) {
                Log.e(TAG, "loadTypes exception", e)
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Creates batch and immediately forwards backend-generated notifications (low grape stock)
     * to UI so it can show system notifications.
     */
    fun create(
        wineTypeId: Long,
        plannedLiters: Double,
        onNotifications: (List<NotificationDto>) -> Unit = {}
    ) {
        if (wineTypeId <= 0) {
            _error.value = "Please select a wine type."
            Log.e(TAG, "Blocked create(): wineTypeId=$wineTypeId")
            return
        }
        if (plannedLiters <= 0.0) {
            _error.value = "Planned liters must be > 0."
            Log.e(TAG, "Blocked create(): plannedLiters=$plannedLiters")
            return
        }

        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            _created.value = null

            Log.d(TAG, "Create batch: wineTypeId=$wineTypeId plannedLiters=$plannedLiters")

            try {
                val dto = WineBatchCreateDto(
                    wineTypeId = wineTypeId,
                    plannedLiters = plannedLiters
                )

                val res = batchesRepo.create(dto)

                val notifs = res.notifications.orEmpty()
                if (notifs.isNotEmpty()) {
                    onNotifications(notifs)
                }

                _created.value = res
                Log.d(TAG, "Created batch id=${res.id} notifications=${notifs.size}")
            } catch (e: HttpException) {
                val body = safeErrorBody(e)
                Log.e(TAG, "create HTTP ${e.code()} body=$body", e)
                _error.value = buildString {
                    append("Create failed: HTTP ${e.code()}")
                    if (body.isNotBlank()) append(" — $body")
                }
            } catch (e: Exception) {
                Log.e(TAG, "create exception", e)
                _error.value = e.message ?: "Unknown error"
            } finally {
                _saving.value = false
            }
        }
    }

    fun clearCreated() {
        _created.value = null
    }

    private fun safeErrorBody(e: HttpException): String =
        try { e.response()?.errorBody()?.string().orEmpty() } catch (_: Exception) { "" }

    companion object {
        private const val TAG = "CreateWineBatchVM"
    }
}
