package bg.tu.varna.si.winery.ui.batches

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.winery.dto.WineBatchResponseDto

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
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Wine Batches", style = MaterialTheme.typography.titleLarge)
            if (canCreate) {
                OutlinedButton(onClick = onCreate) { Text("Create") }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }

        if (error != null) {
            Text("Error: $error")
            Spacer(Modifier.height(12.dp))
        }

        if (items.isEmpty() && !loading) {
            Text("No batches.")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items) { b ->
                BatchCard(b = b, onClick = { onOpenDetails(b.id) })
            }
        }
    }
}

@Composable
private fun BatchCard(
    b: WineBatchResponseDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(b.wineTypeName, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(6.dp))
            Text("Planned: ${b.plannedLiters} L | Produced: ${b.producedLiters} L")
            Text("Created: ${b.createdAt}")
            Text("By: ${b.createdByFullName ?: "-"}")
        }
    }
}
