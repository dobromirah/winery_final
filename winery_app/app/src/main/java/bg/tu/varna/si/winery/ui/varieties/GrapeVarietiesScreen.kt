package bg.tu.varna.si.winery.ui.varieties

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

@Composable
fun GrapeVarietiesScreen(
    vm: GrapeVarietiesViewModel,
    contentPadding: PaddingValues = PaddingValues()
) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()
    val saving by vm.saving.collectAsState()
    val error by vm.error.collectAsState()

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var yieldText by remember { mutableStateOf("0.0") }
    var criticalText by remember { mutableStateOf("0.0") }

    LaunchedEffect(Unit) { vm.load() }

    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Grape varieties", style = MaterialTheme.typography.titleLarge)

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
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = yieldText,
                    onValueChange = { yieldText = it.replace(',', '.') },
                    label = { Text("Yield liters/kg") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = criticalText,
                    onValueChange = { criticalText = it.replace(',', '.') },
                    label = { Text("Critical min kg") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                val yield = yieldText.toDoubleOrNull()
                val critical = criticalText.toDoubleOrNull()
                val canCreate = name.isNotBlank() && yield != null && yield > 0 && critical != null && critical >= 0 && !saving

                Button(
                    enabled = canCreate,
                    onClick = {
                        vm.create(
                            name = name,
                            category = category.ifBlank { null },
                            yieldLitersPerKg = yield ?: 0.0,
                            criticalMinKg = critical ?: 0.0
                        )
                        name = ""
                        category = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (saving) "Saving..." else "Create variety")
                }
            }
        }

        Text("All varieties", style = MaterialTheme.typography.titleMedium)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { v ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(v.name, style = MaterialTheme.typography.titleSmall)
                        Text("Category: ${v.category ?: "-"}")
                        Text("Yield: ${v.yieldLitersPerKg} L/kg")
                        Text("Critical min: ${v.criticalMinKg} kg")
                    }
                }
            }
        }
    }
}
