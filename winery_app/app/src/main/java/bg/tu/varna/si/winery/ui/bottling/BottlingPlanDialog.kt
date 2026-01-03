package bg.tu.varna.si.winery.ui.bottling

import androidx.compose.foundation.layout.*
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
    leftoverLiters: Double?,
    loading: Boolean,
    saving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onRecalculatePrefer750: () -> Unit,
    onOnly750: () -> Unit, // ти трябва id на 750 за това (виж по-долу)
    onSetCount: (bottleTypeId: Long, newCount: Int) -> Unit,
    onConfirm: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bottling plan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())

                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }

                if (plan.isEmpty() && !loading) {
                    Text("No plan yet. Tap Recalculate.")
                } else {
                    plan.forEach { row ->
                        PlanRow(
                            row = row,
                            onMinus = { onSetCount(row.bottleTypeId, row.count - 1) },
                            onPlus = { onSetCount(row.bottleTypeId, row.count + 1) }
                        )
                    }
                }

                if (leftoverLiters != null) {
                    Text("Leftover: ${formatNumber(leftoverLiters)} L", style = MaterialTheme.typography.bodySmall)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onRecalculatePrefer750, enabled = !loading && !saving) {
                        Text("Recalculate")
                    }
                    OutlinedButton(onClick = onOnly750, enabled = !loading && !saving) {
                        Text("Only 750ml")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = !saving && plan.any { it.count > 0 }) {
                Text(if (saving) "Saving..." else "Confirm bottling")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun PlanRow(
    row: BottlePlanItemDto,
    onMinus: () -> Unit,
    onPlus: () -> Unit
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
                OutlinedButton(onClick = onPlus) { Text("+") }
            }
        }
    }
}

private fun formatNumber(v: Double): String {
    return if (abs(v - v.toLong()) < 1e-9) v.toLong().toString() else "%.2f".format(v)
}
