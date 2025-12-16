package bg.tu.varna.si.winery.ui.warehouse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.winery.dto.BottleStockReportDto
import bg.tu.varna.si.winery.dto.GrapeStockReportDto

@Composable
fun WarehouseStockScreen(
    vm: WarehouseStockViewModel,
    contentPadding: PaddingValues = PaddingValues()
) {
    val grapes by vm.grapes.collectAsState()
    val bottles by vm.bottles.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { vm.load() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Warehouse Stock", style = MaterialTheme.typography.titleLarge)

            if (loading) {
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            if (error != null) {
                Spacer(Modifier.height(10.dp))
                Text("Error: $error")
            }
        }

        item { Text("Grapes", style = MaterialTheme.typography.titleMedium) }
        items(grapes) { g -> GrapeRow(g) }

        item { Spacer(Modifier.height(8.dp)) }
        item { Text("Bottles", style = MaterialTheme.typography.titleMedium) }
        items(bottles) { b -> BottleRow(b) }
    }
}

@Composable
private fun GrapeRow(g: GrapeStockReportDto) {
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

@Composable
private fun BottleRow(b: BottleStockReportDto) {
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
