package bg.tu.varna.si.winery.ui.batches

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.winery.dto.WineBatchGrapeUsageDto

@Composable
fun WineBatchDetailsScreen(
    id: Long,
    vm: WineBatchDetailsViewModel,
    contentPadding: PaddingValues = PaddingValues()
) {
    val item by vm.item.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(id) { vm.load(id) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Batch details", style = MaterialTheme.typography.titleLarge)

        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        if (error != null) {
            Text("Error: $error")
        }

        val b = item ?: return@Column

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text(b.wineTypeName, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(6.dp))
                Text("Planned: ${b.plannedLiters} L")
                Text("Produced: ${b.producedLiters} L")
                Text("Created: ${b.createdAt}")
                Text("By: ${b.createdByFullName ?: "-"} (id=${b.createdById ?: "-"})")
            }
        }

        if (b.grapeUsage.isNotEmpty()) {
            Text("Grape usage", style = MaterialTheme.typography.titleMedium)
            b.grapeUsage.forEach { u -> UsageRow(u) }
        } else {
            Text("No grape usage data.")
        }
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
            Text("${u.kgUsed} kg")
        }
    }
}
