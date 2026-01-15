package bg.tu.varna.si.winery.ui.winetypes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.winery.dto.WineTypeDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineTypesAdminScreen(
    vm: WineTypesAdminViewModel,
    contentPadding: PaddingValues = PaddingValues()
) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()
    val saving by vm.saving.collectAsState()
    val error by vm.error.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var colorExpanded by remember { mutableStateOf(false) }
    var color by remember { mutableStateOf("WHITE") }

    LaunchedEffect(Unit) { vm.load() }

    Column(
        Modifier.fillMaxSize().padding(contentPadding).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Wine types", style = MaterialTheme.typography.titleLarge)

        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (error != null) Text("Error: $error", color = MaterialTheme.colorScheme.error)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = colorExpanded,
                    onExpandedChange = { colorExpanded = !colorExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        value = color,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Color") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = colorExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = colorExpanded,
                        onDismissRequest = { colorExpanded = false }
                    ) {
                        listOf("WHITE", "RED", "ROSE").forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c) },
                                onClick = {
                                    color = c
                                    colorExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                val canCreate = name.isNotBlank() && !saving

                Button(
                    enabled = canCreate,
                    onClick = {
                        vm.create(name = name, color = color, description = description)
                        name = ""
                        description = ""
                        color = "WHITE"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (saving) "Saving..." else "Create wine type")
                }
            }
        }

        Text("All wine types", style = MaterialTheme.typography.titleMedium)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { wt -> WineTypeRow(wt) }
        }
    }
}

@Composable
private fun WineTypeRow(wt: WineTypeDto) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(wt.name, style = MaterialTheme.typography.titleSmall)
            Text("Color: ${wt.color}")
            Text("Description: ${wt.description ?: "-"}")
        }
    }
}
