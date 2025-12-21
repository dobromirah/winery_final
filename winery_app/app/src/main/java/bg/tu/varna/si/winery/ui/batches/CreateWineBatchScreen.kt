package bg.tu.varna.si.winery.ui.batches

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.winery.dto.WineTypeDto
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

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

    // ✅ NEW: max liters based on stock + recipe (from VM)
    val maxLiters by vm.maxLiters.collectAsState()
    val limitInfo by vm.limitInfo.collectAsState()

    var selected by rememberSaveable { mutableStateOf<WineTypeDto?>(null) }
    var typesExpanded by remember { mutableStateOf(false) }

    // ✅ NEW: slider value
    var plannedLiters by rememberSaveable { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) { vm.loadTypes() }

    LaunchedEffect(created) {
        val b = created ?: return@LaunchedEffect
        vm.clearCreated()
        onCreated(b.id)
    }

    // Slider config
    val maxL = (maxLiters ?: 0.0).toFloat()
    val sliderEnabled = selected != null && maxL > 0f && !saving

    // Steps (nice UX): if max >= 10L, use 0.5L step
    val step = if (maxL >= 10f) 0.5f else 0.1f

    fun snapToStep(v: Float): Float {
        if (maxL <= 0f) return 0f
        val clamped = v.coerceIn(0f, maxL)
        val snapped = (ceil(clamped / step) * step)
        return snapped.coerceIn(0f, maxL)
    }

    val canSubmit = selected != null && plannedLiters > 0f && plannedLiters <= maxL && !saving

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

        // Wine type dropdown
        ExposedDropdownMenuBox(
            expanded = typesExpanded,
            onExpandedChange = { typesExpanded = !typesExpanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
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
                                plannedLiters = 0f
                                vm.onWineTypeSelected(id)
                            }
                        }

                    )
                }
            }
        }

        // ---- Stock-based max liters + slider ----
        if (selected != null) {
            if (maxLiters == null) {
                // While max liters is loading (or not fetched yet)
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
                    // Slider
                    Slider(
                        value = plannedLiters.coerceIn(0f, maxL),
                        onValueChange = { plannedLiters = it },
                        enabled = sliderEnabled,
                        valueRange = 0f..maxL
                    )

                    val plannedDisplay = "%.1f".format(plannedLiters)
                    Text("Planned liters: $plannedDisplay L", style = MaterialTheme.typography.titleMedium)

                    // quick chips
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = { plannedLiters = snapToStep(min(maxL, max(0f, maxL * 0.25f))) },
                            label = { Text("25%") },
                            enabled = sliderEnabled
                        )
                        AssistChip(
                            onClick = { plannedLiters = snapToStep(min(maxL, max(0f, maxL * 0.5f))) },
                            label = { Text("50%") },
                            enabled = sliderEnabled
                        )
                        AssistChip(
                            onClick = { plannedLiters = snapToStep(min(maxL, max(0f, maxL * 0.75f))) },
                            label = { Text("75%") },
                            enabled = sliderEnabled
                        )
                        AssistChip(
                            onClick = { plannedLiters = snapToStep(maxL) },
                            label = { Text("Max") },
                            enabled = sliderEnabled
                        )
                    }
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
                Log.e("CreateWineBatchUI", "CLICK create selectedId=$id planned=$plannedLiters max=$maxL")
                vm.create(id, plannedLiters.toDouble())
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
