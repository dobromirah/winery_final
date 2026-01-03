package bg.tu.varna.si.winery.ui.bottling

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.BottledWinesRepo
import bg.tu.varna.si.winery.dto.BottleApplyRequestDto
import bg.tu.varna.si.winery.dto.BottlePlanItemDto
import bg.tu.varna.si.winery.dto.AutoBottlePlanResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class BottlingViewModel(private val repo: BottledWinesRepo) : ViewModel() {

    private val _plan = MutableStateFlow<List<BottlePlanItemDto>>(emptyList())
    val plan: StateFlow<List<BottlePlanItemDto>> = _plan

    private val _leftover = MutableStateFlow<Double?>(null)
    val leftover: StateFlow<Double?> = _leftover

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // preference: 750ml priority (backend also does it)
    fun loadPlanPrefer750(batchId: Long) {
        loadPlan(batchId = batchId, preferredBottleTypeId = null, allowedBottleTypeIds = null)
    }

    fun loadPlanOnlyBottleType(batchId: Long, bottleTypeId: Long) {
        loadPlan(batchId = batchId, preferredBottleTypeId = bottleTypeId, allowedBottleTypeIds = listOf(bottleTypeId))
    }

    fun loadPlan(
        batchId: Long,
        preferredBottleTypeId: Long? = null,
        allowedBottleTypeIds: List<Long>? = null
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val res: AutoBottlePlanResponseDto = repo.plan(batchId, preferredBottleTypeId, allowedBottleTypeIds)
                _plan.value = res.items
                _leftover.value = res.leftoverLiters
            } catch (e: HttpException) {
                val body = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
                val url = try { e.response()?.raw()?.request?.url.toString() } catch (_: Exception) { "?" }
                Log.e("BOTTLING", "PLAN HTTP ${e.code()} url=$url body=$body", e)
                _error.value = "HTTP ${e.code()} ${body ?: ""}".trim()
            } catch (e: Exception) {
                Log.e("BOTTLING", "PLAN ERR", e)
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun setCount(bottleTypeId: Long, newCount: Int) {
        _plan.value = _plan.value.map {
            if (it.bottleTypeId == bottleTypeId) it.copy(count = newCount.coerceAtLeast(0)) else it
        }
    }

    fun apply(batchId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            try {
                val items = _plan.value
                    .filter { it.count > 0 }
                    .map { BottleApplyRequestDto.Item(bottleTypeId = it.bottleTypeId, count = it.count) }

                repo.apply(batchId, items)
                onSuccess()
            } catch (e: HttpException) {
                val body = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
                val url = try { e.response()?.raw()?.request?.url.toString() } catch (_: Exception) { "?" }
                Log.e("BOTTLING", "APPLY HTTP ${e.code()} url=$url body=$body", e)
                _error.value = "HTTP ${e.code()} ${body ?: ""}".trim()
            } catch (e: Exception) {
                Log.e("BOTTLING", "APPLY ERR", e)
                _error.value = e.message ?: "Unknown error"
            } finally {
                _saving.value = false
            }
        }
    }

    fun clear() {
        _plan.value = emptyList()
        _leftover.value = null
        _error.value = null
    }
}
