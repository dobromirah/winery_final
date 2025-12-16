package bg.tu.varna.si.winery.ui.batches

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import bg.tu.varna.si.winery.dto.WineTypeDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWineBatchScreen(
    vm: CreateWineBatchViewModel,
    contentPadding: PaddingValues = PaddingValues(),
    onCreated: (createdBatchId: Long) -> Unit
) {
    val types by vm.types.collectAsState()
    val loading by vm.loading.collectAsState()
    val saving by vm.saving.collectAsState()
    val error by vm.error.collectAsState()
    val created by vm.created.collectAsState()

    var selected by remember { mutableStateOf<WineTypeDto?>(null) }
    var plannedText by remember { mutableStateOf("") }
    var typesExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.loadTypes() }

    // on success
    LaunchedEffect(created) {
        val b = created ?: return@LaunchedEffect
        vm.clearCreated()
        onCreated(b.id)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create Wine Batch", style = MaterialTheme.typography.titleLarge)

        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        if (error != null) {
            Text("Error: $error")
        }

        // Wine type dropdown
        ExposedDropdownMenuBox(
            expanded = typesExpanded,
            onExpandedChange = { typesExpanded = !typesExpanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                value = selected?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Wine type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typesExpanded) }
            )

            ExposedDropdownMenu(
                expanded = typesExpanded,
                onDismissRequest = { typesExpanded = false }
            ) {
                types.forEach { t ->
                    DropdownMenuItem(
                        text = { Text(t.name) },
                        onClick = {
                            selected = t
                            typesExpanded = false
                        }
                    )
                }
            }
        }

        // Planned liters
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = plannedText,
            onValueChange = { plannedText = it.replace(',', '.') },
            label = { Text("Planned liters") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        val planned = plannedText.toDoubleOrNull()
        val canSubmit = selected != null && planned != null && planned > 0 && !saving

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = canSubmit,
            onClick = { vm.create(selected!!.id, planned!!) }
        ) {
            if (saving) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Creating…")
                }
            } else {
                Text("Create")
            }
        }
    }
}
