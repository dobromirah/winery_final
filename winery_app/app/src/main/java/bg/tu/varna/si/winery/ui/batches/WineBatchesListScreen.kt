package bg.tu.varna.si.winery.ui.batches

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import kotlin.math.abs

@Composable
fun WineBatchesListScreen(
    vm: WineBatchesListViewModel,
    contentPadding: PaddingValues = PaddingValues(),
    canCreate: Boolean,
    onCreate: () -> Unit,
    onOpenDetails: (Long) -> Unit
) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { vm.load() }

    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Wine batches", style = MaterialTheme.typography.titleLarge)

            if (canCreate) {
                Button(onClick = onCreate) { Text("Create") }
            }
        }

        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())

        if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }

        if (!loading && error == null && items.isEmpty()) {
            Text("No batches yet.")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items) { b ->
                BatchRow(
                    b = b,
                    onClick = { onOpenDetails(b.id) }
                )
            }
        }
    }
}

@Composable
private fun BatchRow(
    b: WineBatchResponseDto,
    onClick: () -> Unit
) {
    val (cardColor, chipBg, chipText) = batchStatusColors(b.status)

    val remaining = (b.producedLiters - b.bottledLiters).coerceAtLeast(0.0)
    val leftover = remaining

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(b.wineTypeName, style = MaterialTheme.typography.titleSmall)
                StatusChip(status = b.status, bg = chipBg, text = chipText)
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Planned: ${formatNumber(b.plannedLiters)} L")
                Text("Produced: ${formatNumber(b.producedLiters)} L")
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Bottled: ${formatNumber(b.bottledLiters)} L")
                Text("Remaining: ${formatNumber(remaining)} L")
            }

            Text("Created: ${b.createdAt}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun StatusChip(status: String, bg: Color, text: Color) {
    AssistChip(
        onClick = {},
        label = { Text(status) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = bg,
            labelColor = text
        )
    )
}

@Composable
private fun batchStatusColors(statusRaw: String): Triple<Color, Color, Color> {
    val cs = MaterialTheme.colorScheme
    return when (statusRaw.trim().uppercase()) {
        "CANCELLED" -> Triple(cs.errorContainer, cs.error, cs.onError)
        "COMPLETED", "BOTTLED" -> Triple(cs.tertiaryContainer, cs.tertiary, cs.onTertiary)
        "PLANNED", "ACTIVE", "IN_PROGRESS", "IN_PRODUCTION" -> Triple(cs.primaryContainer, cs.primary, cs.onPrimary)
        else -> Triple(cs.surfaceVariant, cs.secondaryContainer, cs.onSecondaryContainer)
    }
}

private fun formatNumber(v: Double): String {
    return if (abs(v - v.toLong()) < 1e-9) v.toLong().toString() else "%.2f".format(v)
}
