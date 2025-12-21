package bg.tu.varna.si.winery.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.tu.varna.si.winery.data.repo.WineRecipesRepo
import bg.tu.varna.si.winery.dto.WineRecipeCreateDto
import bg.tu.varna.si.winery.dto.WineRecipeResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class WineRecipeAdminViewModel(
    private val repo: WineRecipesRepo
) : ViewModel() {

    private val _rows = MutableStateFlow<List<WineRecipeResponseDto>>(emptyList())
    val rows: StateFlow<List<WineRecipeResponseDto>> = _rows

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load(wineTypeId: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _rows.value = repo.byWineType(wineTypeId)
            } catch (e: HttpException) {
                _error.value = "HTTP ${e.code()} (${e.message()})"
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addRow(wineTypeId: Long, grapeVarietyId: Long, kgPerLiter: Double) {
        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            try {
                repo.create(WineRecipeCreateDto(wineTypeId, grapeVarietyId, kgPerLiter))
                load(wineTypeId)
            } catch (e: HttpException) {
                _error.value = "HTTP ${e.code()} (${e.message()})"
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _saving.value = false
            }
        }
    }

    fun deleteRow(wineTypeId: Long, recipeRowId: Long) {
        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            try {
                repo.delete(recipeRowId)
                load(wineTypeId)
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
