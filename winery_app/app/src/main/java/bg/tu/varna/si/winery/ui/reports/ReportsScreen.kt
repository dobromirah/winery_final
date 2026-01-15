package bg.tu.varna.si.winery.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.winery.dto.BottleStockReportDto
import bg.tu.varna.si.winery.dto.BottledWineReportDto
import bg.tu.varna.si.winery.dto.GrapeStockReportDto
import bg.tu.varna.si.winery.dto.WineBatchGrapeUsageDto
import bg.tu.varna.si.winery.dto.WineBatchResponseDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReportsScreen(
    vm: ReportsViewModel,
    contentPadding: PaddingValues = PaddingValues()
) {
    val grapes by vm.grapes.collectAsState()
    val bottles by vm.bottles.collectAsState()
    val bottledWine by vm.bottledWine.collectAsState()
    val batches by vm.batches.collectAsState()

    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    // 0=Grapes, 1=Bottles, 2=Bottled wine, 3=Batches
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Grapes", "Bottles", "Bottled wine", "Batches")

    val dateFmt = remember { DateTimeFormatter.ISO_LOCAL_DATE }
    var fromDateText by remember { mutableStateOf("") }
    var toDateText by remember { mutableStateOf("") }

    LaunchedEffect(tabIndex) {
        when (tabIndex) {
            0, 1 -> vm.loadAllStock()
            2 -> vm.loadBottledWine()
            3 -> vm.loadBatches(
                from = fromDateText.takeIf { it.isNotBlank() },
                to = toDateText.takeIf { it.isNotBlank() }
            )
        }
    }

    // Да не се виждат канселираните в репорти
    val visibleBatches = remember(batches) {
        batches.filter { it.status.toString() != "CANCELLED" }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = tabIndex == idx,
                    onClick = { tabIndex = idx },
                    text = { Text(title) }
                )
            }
        }

        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text("Error: $error", modifier = Modifier.padding(16.dp))
        }

        when (tabIndex) {
            0 -> GrapesTab(grapes)
            1 -> BottlesTab(bottles)
            2 -> BottledWineTab(bottledWine)
            3 -> BatchesTab(
                items = visibleBatches,
                fromText = fromDateText,
                toText = toDateText,
                onFromChange = { fromDateText = it },
                onToChange = { toDateText = it },
                onToday = {
                    val today = LocalDate.now().format(dateFmt)
                    fromDateText = today
                    toDateText = today
                    vm.loadBatches(from = today, to = today)
                },
                onLast7Days = {
                    val to = LocalDate.now()
                    val from = to.minusDays(6)
                    val fromS = from.format(dateFmt)
                    val toS = to.format(dateFmt)
                    fromDateText = fromS
                    toDateText = toS
                    vm.loadBatches(from = fromS, to = toS)
                },
                onClear = {
                    fromDateText = ""
                    toDateText = ""
                    vm.loadBatches(from = null, to = null)
                },
                onApply = {
                    val fromOk = fromDateText.isBlank() || isValidDate(fromDateText)
                    val toOk = toDateText.isBlank() || isValidDate(toDateText)

                    if (!fromOk || !toOk) {
                        return@BatchesTab
                    }

                    vm.loadBatches(
                        from = fromDateText.takeIf { it.isNotBlank() },
                        to = toDateText.takeIf { it.isNotBlank() }
                    )
                }
            )
        }
    }
}

@Composable
private fun GrapesTab(items: List<GrapeStockReportDto>) {
    if (items.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("No grape stock data.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { g ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("${g.varietyName} (${g.category})", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(6.dp))
                    Text("Current: ${g.currentKg} kg  |  Min: ${g.criticalMinKg} kg")
                    if (g.belowMinimum) {
                        Spacer(Modifier.height(6.dp))
                        Text("⚠️ Below minimum", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun BottlesTab(items: List<BottleStockReportDto>) {
    if (items.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("No bottle stock data.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { b ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("${b.description} (${b.volumeMl}ml)", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(6.dp))
                    Text("Current: ${b.currentQty}  |  Min: ${b.criticalMinQty}")
                    if (b.belowMinimum) {
                        Spacer(Modifier.height(6.dp))
                        Text("⚠️ Below minimum", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}


@Composable
private fun BottledWineTab(items: List<BottledWineReportDto>) {
    if (items.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("No bottled wine data.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { x ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(x.wineTypeName, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(6.dp))
                    Text("Batch ID: ${x.batchId}")
                    Text("Bottle: ${x.bottleDescription} (${x.volumeMl}ml)")
                    Spacer(Modifier.height(6.dp))
                    Text("Quantity bottles: ${x.quantityBottles}", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun BatchesTab(
    items: List<WineBatchResponseDto>,
    fromText: String,
    toText: String,
    onFromChange: (String) -> Unit,
    onToChange: (String) -> Unit,
    onToday: () -> Unit,
    onLast7Days: () -> Unit,
    onClear: () -> Unit,
    onApply: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Text("Filter by date (YYYY-MM-DD):", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = fromText,
            onValueChange = onFromChange,
            label = { Text("From (YYYY-MM-DD)") },
            supportingText = {
                if (fromText.isNotBlank() && !isValidDate(fromText)) Text("Invalid date format")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = toText,
            onValueChange = onToChange,
            label = { Text("To (YYYY-MM-DD)") },
            supportingText = {
                if (toText.isNotBlank() && !isValidDate(toText)) Text("Invalid date format")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onApply, modifier = Modifier.weight(1f)) { Text("Apply") }
            OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) { Text("Clear") }
        }

        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onToday, modifier = Modifier.weight(1f)) { Text("Today") }
            OutlinedButton(onClick = onLast7Days, modifier = Modifier.weight(1f)) { Text("Last 7 days") }
        }

        Spacer(Modifier.height(16.dp))

        if (items.isEmpty()) {
            Text("No batches for this period.")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items) { b ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(b.wineTypeName, style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(6.dp))
                        Text("Planned: ${b.plannedLiters} L  |  Produced: ${b.producedLiters} L")
                        Text("Created at: ${b.createdAt}")
                        Text("Created by: ${b.createdByFullName ?: "-"}")

                        if (b.grapeUsage.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Grape usage:", style = MaterialTheme.typography.labelLarge)
                            b.grapeUsage.forEach { u -> GrapeUsageRow(u) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GrapeUsageRow(u: WineBatchGrapeUsageDto) {
    Text("- ${u.grapeVarietyName}: ${u.kgUsed} kg")
}

private fun isValidDate(s: String): Boolean {
    return try {
        LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE)
        true
    } catch (_: Exception) {
        false
    }
}
