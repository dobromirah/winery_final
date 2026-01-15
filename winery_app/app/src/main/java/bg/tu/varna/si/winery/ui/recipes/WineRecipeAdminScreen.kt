package bg.tu.varna.si.winery.ui.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.winery.dto.GrapeVarietyResponseDto
import bg.tu.varna.si.winery.dto.WineRecipeResponseDto
import bg.tu.varna.si.winery.dto.WineTypeDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineRecipeAdminScreen(
    recipeVm: WineRecipeAdminViewModel,
    wineTypesVm: WineTypesPickerViewModel,
    grapeVarietiesVm: GrapeVarietiesPickerViewModel,
    contentPadding: PaddingValues = PaddingValues()
) {
    val wineTypes by wineTypesVm.items.collectAsState()
    val wineTypesLoading by wineTypesVm.loading.collectAsState()

    val varieties by grapeVarietiesVm.items.collectAsState()
    val varietiesLoading by grapeVarietiesVm.loading.collectAsState()

    val rows by recipeVm.rows.collectAsState()
    val loading by recipeVm.loading.collectAsState()
    val saving by recipeVm.saving.collectAsState()
    val error by recipeVm.error.collectAsState()

    var selectedWineType by rememberSaveable { mutableStateOf<WineTypeDto?>(null) }
    var wineTypesExpanded by remember { mutableStateOf(false) }

    var selectedVariety by rememberSaveable { mutableStateOf<GrapeVarietyResponseDto?>(null) }
    var varietiesExpanded by remember { mutableStateOf(false) }
    var kgPerLiterText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        wineTypesVm.load()
        grapeVarietiesVm.load()
    }

    LaunchedEffect(selectedWineType?.id) {
        val id = selectedWineType?.id ?: return@LaunchedEffect
        recipeVm.load(id)
        selectedVariety = null
        kgPerLiterText = ""
    }

    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(contentPadding)
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Wine recipes", style = MaterialTheme.typography.titleLarge)

        if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }

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

        val wineTypeId = selectedWineType?.id
        if (wineTypeId == null) {
            Text("Pick a wine type to view / edit its recipe.")
            return@Column
        }

        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        Text("2) Add recipe row", style = MaterialTheme.typography.titleMedium)

        if (varietiesLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

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
                recipeVm.addRow(
                    wineTypeId = wineTypeId,
                    grapeVarietyId = gvId,
                    kgPerLiter = kgPerLiter!!
                )
                kgPerLiterText = ""
                selectedVariety = null
            }
        ) {
            Text(if (saving) "Saving..." else "Add row")
        }

        HorizontalDivider()

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

        Spacer(Modifier.height(12.dp))
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
