package bg.tu.varna.si.winery.ui.bottling

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.winery.dto.BottlePlanItemDto
import kotlin.math.abs

@Composable
fun BottlingPlanDialog(
    show: Boolean,
    plan: List<BottlePlanItemDto>,
    remainingLiters: Double,
    leftoverLiters: Double?,
    loading: Boolean,
    saving: Boolean,
    error: String?,
    selectedPriorityBottleTypeId: Long?,
    onSetPriorityBottleTypeId: (Long?) -> Unit,
    onRecalculate: (preferredBottleTypeId: Long) -> Unit,
    onOnlySelectedBottle: (bottleTypeId: Long) -> Unit,
    onSetCount: (bottleTypeId: Long, newCount: Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    val plannedLiters = remember(plan) { calcPlannedLiters(plan) }
    val exceedsRemaining = plannedLiters > remainingLiters + 1e-9
    val canConfirm = !saving && plan.any { it.count > 0 } && !exceedsRemaining

    val options = remember(plan) {
        plan.distinctBy { it.bottleTypeId }
            .sortedByDescending { it.volumeMl }
    }

    val selectedOption = options.firstOrNull { it.bottleTypeId == selectedPriorityBottleTypeId }
    val hasSelection = selectedPriorityBottleTypeId != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bottling plan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())

                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Remaining: ${formatNumber(remainingLiters)} L", style = MaterialTheme.typography.bodySmall)
                        Text("Planned: ${formatNumber(plannedLiters)} L", style = MaterialTheme.typography.bodySmall)

                        if (leftoverLiters != null) {
                            Text(
                                "Leftover: ${formatNumber(leftoverLiters)} L",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        if (exceedsRemaining) {
                            Text(
                                "Planned liters exceed remaining liters.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                PriorityBottlePicker(
                    options = options,
                    selected = selectedOption,
                    enabled = !loading && !saving,
                    onSelect = onSetPriorityBottleTypeId
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { onRecalculate(selectedPriorityBottleTypeId!!) },
                        enabled = !loading && !saving && hasSelection
                    ) {
                        Text("Recalculate")
                    }

                    OutlinedButton(
                        onClick = { onOnlySelectedBottle(selectedPriorityBottleTypeId!!) },
                        enabled = !loading && !saving && hasSelection
                    ) {
                        Text("Only selected")
                    }
                }

                if (plan.isEmpty() && !loading) {
                    Text("No plan yet.")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items = plan, key = { it.bottleTypeId }) { row ->
                            val plusEnabled = canAddOneMoreBottle(
                                row = row,
                                remainingLiters = remainingLiters,
                                currentPlannedLiters = plannedLiters
                            )

                            PlanRow(
                                row = row,
                                onMinus = { onSetCount(row.bottleTypeId, (row.count - 1).coerceAtLeast(0)) },
                                onPlus = { onSetCount(row.bottleTypeId, row.count + 1) },
                                plusEnabled = plusEnabled
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = canConfirm) {
                Text(if (saving) "Saving..." else "Confirm bottling")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriorityBottlePicker(
    options: List<BottlePlanItemDto>,
    selected: BottlePlanItemDto?,
    enabled: Boolean,
    onSelect: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val label = selected?.let { "${it.description} (${it.volumeMl} ml)" } ?: "Select bottle type"

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Priority bottle", style = MaterialTheme.typography.titleSmall)

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { if (enabled && options.isNotEmpty()) expanded = !expanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    readOnly = true,
                    enabled = enabled && options.isNotEmpty(),
                    value = label,
                    onValueChange = {},
                    label = { Text("Bottle type") }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text("${opt.description} (${opt.volumeMl} ml)") },
                            onClick = {
                                expanded = false
                                onSelect(opt.bottleTypeId)
                            }
                        )
                    }
                }
            }

            if (options.isEmpty()) {
                Text(
                    "No bottle types available.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun PlanRow(
    row: BottlePlanItemDto,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    plusEnabled: Boolean
) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(row.description, style = MaterialTheme.typography.titleSmall)
                Text("${row.volumeMl} ml", style = MaterialTheme.typography.bodySmall)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onMinus, enabled = row.count > 0) { Text("-") }
                Spacer(Modifier.width(10.dp))
                Text("${row.count}")
                Spacer(Modifier.width(10.dp))
                OutlinedButton(onClick = onPlus, enabled = plusEnabled) { Text("+") }
            }
        }
    }
}

private fun calcPlannedLiters(plan: List<BottlePlanItemDto>): Double {
    var liters = 0.0
    for (r in plan) {
        if (r.count <= 0) continue
        liters += (r.count * r.volumeMl) / 1000.0
    }
    return liters
}

private fun canAddOneMoreBottle(
    row: BottlePlanItemDto,
    remainingLiters: Double,
    currentPlannedLiters: Double
): Boolean {
    val addLiters = row.volumeMl / 1000.0
    return currentPlannedLiters + addLiters <= remainingLiters + 1e-9
}

private fun formatNumber(v: Double): String {
    return if (abs(v - v.toLong()) < 1e-9) v.toLong().toString() else "%.2f".format(v)
}
