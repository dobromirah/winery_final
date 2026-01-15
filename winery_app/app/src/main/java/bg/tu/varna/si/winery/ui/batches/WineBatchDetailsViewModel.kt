package bg.tu.varna.si.winery.ui.batches

import android.util.Log
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

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load(id: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _item.value = repo.getById(id)
            } catch (e: HttpException) {
                _error.value = httpErrorText("LOAD", e)
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun setProduced(id: Long, producedLiters: Double) {
        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            try {
                _item.value = repo.setProduced(id, producedLiters)
            } catch (e: HttpException) {
                _error.value = httpErrorText("SET_PRODUCED", e)
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
                _error.value = httpErrorText("CANCEL", e)
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _saving.value = false
            }
        }
    }

    private fun httpErrorText(tag: String, e: HttpException): String {
        val body = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
        val url = try { e.response()?.raw()?.request?.url.toString() } catch (_: Exception) { "?" }
        Log.e("BATCH", "$tag HTTP ${e.code()} url=$url body=$body", e)
        return "HTTP ${e.code()} ${body ?: "(${e.message()})"}".trim()
    }
}
