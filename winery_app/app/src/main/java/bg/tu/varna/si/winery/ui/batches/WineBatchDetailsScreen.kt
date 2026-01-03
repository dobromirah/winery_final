package bg.tu.varna.si.winery.ui.batches

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.winery.dto.WineBatchGrapeUsageDto
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import bg.tu.varna.si.winery.ui.bottling.BottlingPlanDialog
import bg.tu.varna.si.winery.ui.bottling.BottlingViewModel
import kotlin.math.abs

@Composable
fun WineBatchDetailsScreen(
    id: Long,
    vm: WineBatchDetailsViewModel,
    bottlingVm: BottlingViewModel,
    contentPadding: PaddingValues = PaddingValues()
) {
    val item by vm.item.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val saving by vm.saving.collectAsState()

    // bottling vm state
    val plan by bottlingVm.plan.collectAsState()
    val leftover by bottlingVm.leftover.collectAsState()
    val bLoading by bottlingVm.loading.collectAsState()
    val bSaving by bottlingVm.saving.collectAsState()
    val bError by bottlingVm.error.collectAsState()

    var showBottlingDialog by remember { mutableStateOf(false) }

    LaunchedEffect(id) { vm.load(id) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Batch details", style = MaterialTheme.typography.titleLarge)

        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())

        if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }

        val b = item
        if (b == null) {
            if (!loading && error == null) Text("No data.")
            return@Column
        }

        BatchSummaryCard(
            b = b,
            saving = saving,
            onSetProduced = { produced ->
                vm.setProduced(id = b.id, producedLiters = produced)
            },
            onCancel = {
                vm.cancel(b.id)
            }
        )

        Spacer(Modifier.height(6.dp))

        Text("Grape usage", style = MaterialTheme.typography.titleMedium)

        if (b.grapeUsage.isEmpty()) {
            Text("No grape usage data.")
        } else {
            b.grapeUsage.forEach { u -> UsageRow(u) }
        }

        Spacer(Modifier.height(8.dp))
        Text("Bottling", style = MaterialTheme.typography.titleMedium)

        if (bError != null) {
            Text("Bottling error: $bError", color = MaterialTheme.colorScheme.error)
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !bSaving && !bLoading,
            onClick = {
                showBottlingDialog = true
                bottlingVm.loadPlanPrefer750(b.id) // default: priority 750
            }
        ) {
            Text(if (bLoading) "Loading plan..." else "Plan bottling (prefer 750ml)")
        }

        // try detect 750 typeId from current plan
        val type750 = plan.firstOrNull { it.volumeMl == 750 }?.bottleTypeId

        BottlingPlanDialog(
            show = showBottlingDialog,
            plan = plan,
            leftoverLiters = leftover,
            loading = bLoading,
            saving = bSaving,
            error = bError,
            onDismiss = { showBottlingDialog = false },
            onRecalculatePrefer750 = { bottlingVm.loadPlanPrefer750(b.id) },
            onOnly750 = {
                if (type750 != null) bottlingVm.loadPlanOnlyBottleType(b.id, type750)
                else bottlingVm.loadPlanPrefer750(b.id) // fallback
            },
            onSetCount = { bottleTypeId, newCount ->
                bottlingVm.setCount(bottleTypeId, newCount)
            },
            onConfirm = {
                bottlingVm.apply(b.id) {
                    showBottlingDialog = false
                    vm.load(b.id) // refresh details
                }
            }
        )
    }
}

@Composable
private fun BatchSummaryCard(
    b: WineBatchResponseDto,
    saving: Boolean,
    onSetProduced: (Double) -> Unit,
    onCancel: () -> Unit
) {
    var showProducedDialog by remember { mutableStateOf(false) }
    var producedText by remember { mutableStateOf(formatNumber(b.producedLiters)) }
    var localError by remember { mutableStateOf<String?>(null) }

    var showCancelDialog by remember { mutableStateOf(false) }

    val canCancel = b.status != "COMPLETED" && b.status != "CANCELLED" && !saving
    val canEditProduced = b.status != "CANCELLED" && !saving

    Card(Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(b.wineTypeName, style = MaterialTheme.typography.titleSmall)
                StatusChip(b.status)
            }

            Text("Planned: ${formatNumber(b.plannedLiters)} L")
            Text("Produced: ${formatNumber(b.producedLiters)} L")
            Text("Created: ${b.createdAt}")
            Text("By: ${b.createdByFullName ?: "-"} ") //(id=${b.createdById ?: "-"})")

            if (localError != null) {
                Text(localError!!, color = MaterialTheme.colorScheme.error)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    enabled = canEditProduced,
                    onClick = {
                        producedText = formatNumber(b.producedLiters)
                        localError = null
                        showProducedDialog = true
                    }
                ) {
                    if (saving) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Saving…")
                        }
                    } else {
                        Text("Set produced")
                    }
                }

                OutlinedButton(
                    enabled = canCancel,
                    onClick = { showCancelDialog = true }
                ) {
                    Text("Cancel batch")
                }
            }
        }
    }

    if (showProducedDialog) {
        AlertDialog(
            onDismissRequest = { showProducedDialog = false },
            title = { Text("Produced liters") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = producedText,
                        onValueChange = { producedText = it.replace(',', '.') },
                        label = { Text("Liters") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    Text(
                        "Planned limit: ${formatNumber(b.plannedLiters)} L",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val v = producedText.replace(',', '.').toDoubleOrNull()
                        when {
                            v == null -> localError = "Enter a valid number."
                            v < 0 -> localError = "Produced liters must be ≥ 0."
                            v > b.plannedLiters -> localError = "Produced liters cannot exceed planned liters."
                            else -> {
                                localError = null
                                onSetProduced(v)
                                showProducedDialog = false
                            }
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showProducedDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel batch?") },
            text = { Text("This will rollback grape usage back to stock and mark the batch as CANCELLED.") },
            confirmButton = {
                TextButton(
                    enabled = !saving,
                    onClick = {
                        onCancel()
                        showCancelDialog = false
                    }
                ) { Text("Yes, cancel") }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("No") }
            }
        )
    }
}

@Composable
private fun UsageRow(u: WineBatchGrapeUsageDto) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(u.grapeVarietyName)
            Text("${formatNumber(u.kgUsed)} kg")
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    AssistChip(onClick = {}, label = { Text(status) })
}

private fun formatNumber(v: Double): String {
    return if (abs(v - v.toLong()) < 1e-9) v.toLong().toString() else "%.2f".format(v)
}
