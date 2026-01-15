package bg.tu.varna.si.winery.ui.batches

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.winery.dto.WineTypeDto
import bg.tu.varna.si.winery.notifications.WineryNotifier
import kotlin.math.max

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

    val maxLiters by vm.maxLiters.collectAsState()
    val limitInfo by vm.limitInfo.collectAsState()

    val ctx = LocalContext.current

    var selected by rememberSaveable { mutableStateOf<WineTypeDto?>(null) }
    var typesExpanded by remember { mutableStateOf(false) }
    var plannedLitersText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.loadTypes() }

    LaunchedEffect(created) {
        val b = created ?: return@LaunchedEffect
        vm.clearCreated()
        onCreated(b.id)
    }

    val maxL = (maxLiters ?: 0.0).toFloat()

    val plannedLitersValue: Double? = remember(plannedLitersText) {
        plannedLitersText.trim().replace(',', '.').toDoubleOrNull()
    }
    val plannedFloat = (plannedLitersValue ?: 0.0).toFloat()

    val inputEnabled = selected != null && maxL > 0f && !saving

    val isPlannedValid =
        selected != null &&
                plannedLitersValue != null &&
                plannedLitersValue > 0.0 &&
                plannedLitersValue.toFloat() <= maxL

    val canSubmit = isPlannedValid && !saving

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
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }

        ExposedDropdownMenuBox(
            expanded = typesExpanded,
            onExpandedChange = { typesExpanded = !typesExpanded }
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                value = selected?.name ?: "Select wine type",
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
                            val id = t.id
                            if (id != null) {
                                selected = t
                                typesExpanded = false
                                plannedLitersText = ""
                                vm.onWineTypeSelected(id)
                            }
                        }
                    )
                }
            }
        }

        if (selected != null) {
            if (maxLiters == null) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Text("Calculating available liters…", style = MaterialTheme.typography.bodyMedium)
            } else {
                val maxDisplay = "%.1f".format(max(0f, maxL))
                Text("Max possible: $maxDisplay L", style = MaterialTheme.typography.bodyMedium)

                if (!limitInfo.isNullOrBlank()) {
                    Text(limitInfo!!, style = MaterialTheme.typography.bodySmall)
                }

                if (maxL <= 0f) {
                    Text(
                        "Not enough grape stock for this wine type.",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    OutlinedTextField(
                        value = plannedLitersText,
                        onValueChange = { plannedLitersText = it },
                        label = { Text("Planned liters") },
                        placeholder = { Text("e.g. 120") },
                        supportingText = {
                            when {
                                plannedLitersText.isBlank() -> Text("Enter a value between 0 and $maxDisplay L")
                                plannedLitersValue == null -> Text("Invalid number")
                                plannedLitersValue <= 0.0 -> Text("Must be greater than 0")
                                plannedFloat > maxL -> Text("Must be ≤ $maxDisplay L")
                            }
                        },
                        isError = plannedLitersText.isNotBlank() && !isPlannedValid,
                        enabled = inputEnabled,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            Text("Select a wine type to see available liters.", style = MaterialTheme.typography.bodyMedium)
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = canSubmit,
            onClick = {
                val id = selected?.id ?: -1L
                val liters = plannedLitersValue ?: 0.0
                Log.e("CreateWineBatchUI", "CLICK create selectedId=$id planned=$liters max=$maxL")

                vm.create(
                    wineTypeId = id,
                    plannedLiters = liters,
                    onNotifications = { list ->
                        list.forEach { n ->
                            WineryNotifier.show(
                                context = ctx,
                                title = "${n.level}: ${n.type}",
                                message = n.message
                            )
                        }
                    }
                )
            }
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
