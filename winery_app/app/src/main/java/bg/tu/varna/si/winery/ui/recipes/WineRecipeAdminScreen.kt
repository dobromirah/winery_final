package bg.tu.varna.si.winery.ui.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.winery.dto.GrapeVarietyDto
import bg.tu.varna.si.winery.dto.WineRecipeResponseDto
import bg.tu.varna.si.winery.dto.WineTypeDto
import bg.tu.varna.si.winery.ui.varieties.GrapeVarietiesViewModel
import bg.tu.varna.si.winery.ui.batches.CreateWineBatchViewModel

/**
 * Recipe Admin screen (MVP):
 * - Pick WineType
 * - Shows recipe rows
 * - Add row (Variety + kg/L)
 * - Delete row
 *
 * Roles: typically ADMIN only
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineRecipeAdminScreen(
    recipeVm: WineRecipeAdminViewModel,
    wineTypesVm: WineTypesPickerViewModel,
    grapeVarietiesVm: GrapeVarietiesPickerViewModel,
    contentPadding: PaddingValues = PaddingValues()
) {
    // --- Pickers data ---
    val wineTypes by wineTypesVm.items.collectAsState()
    val wineTypesLoading by wineTypesVm.loading.collectAsState()

    val varieties by grapeVarietiesVm.items.collectAsState()
    val varietiesLoading by grapeVarietiesVm.loading.collectAsState()

    // --- Recipe rows ---
    val rows by recipeVm.rows.collectAsState()
    val loading by recipeVm.loading.collectAsState()
    val saving by recipeVm.saving.collectAsState()
    val error by recipeVm.error.collectAsState()

    var selectedWineType by rememberSaveable { mutableStateOf<WineTypeDto?>(null) }
    var wineTypesExpanded by remember { mutableStateOf(false) }

    // Add row form state
    var selectedVariety by rememberSaveable { mutableStateOf<GrapeVarietyDto?>(null) }
    var varietiesExpanded by remember { mutableStateOf(false) }
    var kgPerLiterText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        wineTypesVm.load()
        grapeVarietiesVm.load()
    }

    // When wine type changes -> load recipe
    LaunchedEffect(selectedWineType?.id) {
        val id = selectedWineType?.id ?: return@LaunchedEffect
        recipeVm.load(id)
        // reset add-form
        selectedVariety = null
        kgPerLiterText = ""
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Wine recipes", style = MaterialTheme.typography.titleLarge)

        if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }

        // --- Wine type picker ---
        Text("1) Select wine type", style = MaterialTheme.typography.titleMedium)

        if (wineTypesLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        ExposedDropdownMenuBox(
            expanded = wineTypesExpanded,
            onExpandedChange = { wineTypesExpanded = !wineTypesExpanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                value = selectedWineType?.name ?: "Select wine type",
                onValueChange = {},
                readOnly = true,
                label = { Text("Wine type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = wineTypesExpanded) }
            )

            ExposedDropdownMenu(
                expanded = wineTypesExpanded,
                onDismissRequest = { wineTypesExpanded = false }
            ) {
                wineTypes.forEach { wt ->
                    DropdownMenuItem(
                        text = { Text(wt.name) },
                        onClick = {
                            selectedWineType = wt
                            wineTypesExpanded = false
                        }
                    )
                }
            }
        }

        // If not selected, stop here (UX)
        val wineTypeId = selectedWineType?.id
        if (wineTypeId == null) {
            Text("Pick a wine type to view / edit its recipe.")
            return@Column
        }

        // --- Loading recipe ---
        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        // --- Add row form ---
        Text("2) Add recipe row", style = MaterialTheme.typography.titleMedium)

        if (varietiesLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        // Variety picker
        ExposedDropdownMenuBox(
            expanded = varietiesExpanded,
            onExpandedChange = { varietiesExpanded = !varietiesExpanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                value = selectedVariety?.name ?: "Select grape variety",
                onValueChange = {},
                readOnly = true,
                label = { Text("Grape variety") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = varietiesExpanded) }
            )

            ExposedDropdownMenu(
                expanded = varietiesExpanded,
                onDismissRequest = { varietiesExpanded = false }
            ) {
                varieties.forEach { gv ->
                    DropdownMenuItem(
                        text = { Text(gv.name) },
                        onClick = {
                            selectedVariety = gv
                            varietiesExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = kgPerLiterText,
            onValueChange = { kgPerLiterText = it.replace(',', '.') },
            label = { Text("kg per liter") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        val kgPerLiter = kgPerLiterText.toDoubleOrNull()
        val canAdd = selectedVariety != null && kgPerLiter != null && kgPerLiter > 0 && !saving

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = canAdd,
            onClick = {
                val gvId = selectedVariety?.id ?: return@Button
                recipeVm.addRow(wineTypeId = wineTypeId, grapeVarietyId = gvId, kgPerLiter = kgPerLiter!!)
                kgPerLiterText = ""
                selectedVariety = null
            }
        ) {
            Text(if (saving) "Saving..." else "Add row")
        }

        Divider()

        // --- Recipe list ---
        Text("3) Current recipe", style = MaterialTheme.typography.titleMedium)

        if (rows.isEmpty()) {
            Text("No recipe rows for this wine type.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                rows.forEach { r ->
                    RecipeRowCard(
                        row = r,
                        enabled = !saving,
                        onDelete = { recipeVm.deleteRow(wineTypeId = wineTypeId, recipeRowId = r.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeRowCard(
    row: WineRecipeResponseDto,
    enabled: Boolean,
    onDelete: () -> Unit
) {
    var confirmDelete by remember { mutableStateOf(false) }

    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(row.grapeVarietyName, style = MaterialTheme.typography.titleSmall)
                Text("kg/L: ${format2(row.kgPerLiter)}")
            }

            OutlinedButton(
                enabled = enabled,
                onClick = { confirmDelete = true }
            ) {
                Text("Delete")
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete row?") },
            text = { Text("Remove ${row.grapeVarietyName} from recipe?") },
            confirmButton = {
                TextButton(
                    enabled = enabled,
                    onClick = {
                        onDelete()
                        confirmDelete = false
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Cancel") }
            }
        )
    }
}

private fun format2(v: Double): String = "%.2f".format(v)
