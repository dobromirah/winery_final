package bg.tu.varna.si.winery.ui.warehouse

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottleMovementScreen(
    vm: BottleMovementViewModel,
    contentPadding: PaddingValues = PaddingValues()
) {
    val types by vm.types.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()

    var selectedId by remember { mutableStateOf<Long?>(null) }
    var qtyText by remember { mutableStateOf("") }
    var movementType by remember { mutableStateOf("IN") }

    LaunchedEffect(Unit) { vm.loadBottleTypes() }

    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
    ) {
        Text("Bottle Stock Movement", style = MaterialTheme.typography.titleLarge)
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

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { movementType = "IN" }, enabled = movementType != "IN") { Text("IN") }
            Button(onClick = { movementType = "OUT" }, enabled = movementType != "OUT") { Text("OUT") }
        }

        Spacer(Modifier.height(12.dp))

        var expanded by remember { mutableStateOf(false) }
        val selectedLabel = types.firstOrNull { it.id == selectedId }
            ?.let { "${it.description} (${it.volumeMl}ml)" }
            ?: "Select bottle type"

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Bottle type") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                types.forEach { t ->
                    DropdownMenuItem(
                        text = { Text("${t.description} (${t.volumeMl}ml)") },
                        onClick = {
                            selectedId = t.id
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
            label = { Text("Quantity (bottles)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val tId = selectedId ?: return@Button
                val qty = qtyText.toIntOrNull() ?: return@Button
                vm.submit(tId, qty, movementType)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            Text("Save movement")
        }
    }
}
