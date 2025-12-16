package bg.tu.varna.si.winery.ui.warehouse

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrapeMovementScreen(vm: GrapeMovementViewModel, onLogout: () -> Unit) {
    val varieties by vm.varieties.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()

    var selectedId by remember { mutableStateOf<Long?>(null) }
    var qtyText by remember { mutableStateOf("") }
    var createdByText by remember { mutableStateOf("") }
    var movementType by remember { mutableStateOf("IN") } // default

    LaunchedEffect(Unit) { vm.loadVarieties() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Grape Stock Movement", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }

        if (error != null) {
            Text("Error: $error")
            Spacer(Modifier.height(12.dp))
        }

        if (success != null) {
            Text(success!!)
            Spacer(Modifier.height(12.dp))
        }

        // Movement type
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { movementType = "IN" },
                enabled = movementType != "IN"
            ) { Text("IN") }

            Button(
                onClick = { movementType = "OUT" },
                enabled = movementType != "OUT"
            ) { Text("OUT") }
            Button(onClick = onLogout) {
                Text("Logout")
            }

        }


        Spacer(Modifier.height(12.dp))

        // Variety dropdown
        var expanded by remember { mutableStateOf(false) }
        val selectedLabel = varieties.firstOrNull { it.id == selectedId }?.let { "${it.name} (${it.category})" }
            ?: "Select grape variety"

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Variety") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                varieties.forEach { v ->
                    DropdownMenuItem(
                        text = { Text("${v.name} (${v.category})") },
                        onClick = {
                            selectedId = v.id
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = qtyText,
            onValueChange = { qtyText = it },
            label = { Text("Quantity (kg)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = createdByText,
            onValueChange = { createdByText = it },
            label = { Text("CreatedById (temporary)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val vId = selectedId ?: return@Button
                val qty = qtyText.toDoubleOrNull() ?: return@Button
                val createdBy = createdByText.toLongOrNull() ?: return@Button
                vm.submit(vId, qty, movementType, createdBy)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            Text("Save movement")
        }
    }
}
