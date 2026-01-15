package bg.tu.varna.si.winery.ui.batches

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import bg.tu.varna.si.winery.dto.WineBatchGrapeUsageDto
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import bg.tu.varna.si.winery.notifications.WineryNotifier
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

    val plan by bottlingVm.plan.collectAsState()
    val leftover by bottlingVm.leftover.collectAsState()
    val bLoading by bottlingVm.loading.collectAsState()
    val bSaving by bottlingVm.saving.collectAsState()
    val bError by bottlingVm.error.collectAsState()

    val ctx = LocalContext.current

    var showBottlingDialog by remember { mutableStateOf(false) }
    var priorityBottleTypeId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(id) { vm.load(id) }

    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(contentPadding)
            .padding(16.dp)
            .imePadding(),
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

        val status = b.status.trim().uppercase()
        val isCancelled = status == "CANCELLED"
        val isBottled = status == "BOTTLED"
        val isCompleted = status == "COMPLETED"

        val remainingLiters = (b.producedLiters - b.bottledLiters).coerceAtLeast(0.0)
        val hasProducedWine = b.producedLiters > 0.0
        val hasRemainingToBottle = remainingLiters > 0.0

        BatchSummaryCard(
            b = b,
            saving = saving,
            onSetProduced = { produced ->
                vm.setProduced(id = b.id, producedLiters = produced)
            },
            onCancel = { vm.cancel(b.id) }
        )

        Card(Modifier.fillMaxWidth()) {
            Row(
                Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Produced: ${formatNumber(b.producedLiters)} L ")
                Text("Bottled: ${formatNumber(b.bottledLiters)} L ")
                Text("Remaining: ${formatNumber(remainingLiters)} L")
            }
        }

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

        val canPlanBottling =
            !bSaving && !bLoading &&
                    !isCancelled && !isBottled &&
                    hasProducedWine && hasRemainingToBottle

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = canPlanBottling,
            onClick = {
                showBottlingDialog = true
                bottlingVm.loadPlanDefault(b.id)
            }
        ) {
            val label = when {
                bLoading -> "Loading plan..."
                isCancelled -> "Batch is cancelled"
                isBottled -> "Already bottled"
                !hasProducedWine -> "No produced wine to bottle"
                !hasRemainingToBottle -> "Nothing left to bottle"
                isCompleted -> "Plan bottling"
                else -> "Plan bottling"
            }
            Text(label)
        }

        BottlingPlanDialog(
            show = showBottlingDialog,
            plan = plan,
            remainingLiters = remainingLiters,
            leftoverLiters = leftover,
            loading = bLoading,
            saving = bSaving,
            error = bError,

            selectedPriorityBottleTypeId = priorityBottleTypeId,
            onSetPriorityBottleTypeId = { priorityBottleTypeId = it },

            onRecalculate = { preferredId ->
                bottlingVm.loadPlanPrefer(b.id, preferredId)
            },
            onOnlySelectedBottle = { bottleTypeId ->
                bottlingVm.loadPlanOnlyBottleType(b.id, bottleTypeId)
            },

            onSetCount = { bottleTypeId, newCount ->
                bottlingVm.setCount(bottleTypeId, newCount)
            },

            onConfirm = {
                bottlingVm.apply(
                    batchId = b.id,
                    onNotifications = { list ->
                        list.forEach { n ->
                            WineryNotifier.show(
                                context = ctx,
                                title = "${n.level}: ${n.type}",
                                message = n.message
                            )
                        }
                    },
                    onSuccess = {
                        WineryNotifier.show(
                            context = ctx,
                            title = "Bottling completed",
                            message = "Batch #${b.id} bottled successfully."
                        )
                        showBottlingDialog = false
                        vm.load(b.id)
                    }
                )
            },
            onDismiss = { showBottlingDialog = false }
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

    val status = b.status.trim().uppercase()
    val isLockedStatus = status == "COMPLETED" || status == "CANCELLED" || status == "BOTTLED"

    val canEditProduced = !isLockedStatus && !saving
    val canCancel = !isLockedStatus && !saving

    val colors = statusColors(b.status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg)
    ) {
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
                StatusChip(status = b.status)
            }

            Text("Planned: ${formatNumber(b.plannedLiters)} L")
            Text("Produced: ${formatNumber(b.producedLiters)} L")
            Text("Bottled: ${formatNumber(b.bottledLiters)} L")
            Text("Created: ${b.createdAt}")
            Text("By: ${b.createdByFullName ?: "-"}")

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
                            v < 0 -> localError = "Produced liters must be greater than 0."
                            v > b.plannedLiters -> localError = "Produced liters cannot exceed planned liters."
                            v < b.producedLiters -> localError = "The latest production must be >= the previous one (" + b.producedLiters + ")."
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
                    enabled = !saving && !isLockedStatus,
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
            Text("${u.grapeVarietyName}: ${formatNumber(u.kgUsed)} kg")
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val colors = statusColors(status)

    AssistChip(
        onClick = {},
        label = { Text(status) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = colors.chipBg,
            labelColor = colors.chipText
        )
    )
}

private data class StatusUiColors(
    val cardBg: Color,
    val chipBg: Color,
    val chipText: Color
)

@Composable
private fun statusColors(status: String): StatusUiColors {
    val cs = MaterialTheme.colorScheme

    return when (status.trim().uppercase()) {
        "CANCELLED" -> StatusUiColors(
            cardBg = cs.errorContainer,
            chipBg = cs.error,
            chipText = cs.onError
        )
        "COMPLETED", "BOTTLED" -> StatusUiColors(
            cardBg = cs.tertiaryContainer,
            chipBg = cs.tertiary,
            chipText = cs.onTertiary
        )
        "PLANNED", "ACTIVE", "IN_PROGRESS", "IN_PRODUCTION" -> StatusUiColors(
            cardBg = cs.primaryContainer,
            chipBg = cs.primary,
            chipText = cs.onPrimary
        )
        else -> StatusUiColors(
            cardBg = cs.surfaceVariant,
            chipBg = cs.secondaryContainer,
            chipText = cs.onSecondaryContainer
        )
    }
}

private fun formatNumber(v: Double): String {
    return if (abs(v - v.toLong()) < 1e-9) v.toLong().toString() else "%.2f".format(v)
}
