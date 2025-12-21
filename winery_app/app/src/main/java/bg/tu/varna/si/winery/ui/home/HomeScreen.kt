package bg.tu.varna.si.winery.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    roles: Set<String>,
    unreadCount: Int,

    onOpenReports: () -> Unit,
    onOpenWarehouseStock: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenGrapeMovement: () -> Unit,
    onOpenBottleMovement: () -> Unit,
    onOpenBatches: () -> Unit,

    // ➕ NEW
    onOpenGrapeVarieties: () -> Unit,
    onOpenWineRecipes: () -> Unit
) {
    fun hasAny(vararg r: String) = r.any { roles.contains(it) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Winery", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        // ---------------- Reports ----------------
        if (hasAny("ADMIN", "WAREHOUSE_MANAGER", "OPERATOR")) {
            Button(onClick = onOpenReports, modifier = Modifier.fillMaxWidth()) {
                Text("Reports")
            }
            Spacer(Modifier.height(10.dp))
        }

        // ---------------- Warehouse stock ----------------
        if (hasAny("ADMIN", "WAREHOUSE_MANAGER", "OPERATOR")) {
            Button(onClick = onOpenWarehouseStock, modifier = Modifier.fillMaxWidth()) {
                Text("Warehouse Stock")
            }
            Spacer(Modifier.height(10.dp))
        }

        // ---------------- Notifications ----------------
        if (hasAny("ADMIN", "WAREHOUSE_MANAGER", "OPERATOR")) {
            Button(onClick = onOpenNotifications, modifier = Modifier.fillMaxWidth()) {
                Text(if (unreadCount > 0) "Notifications ($unreadCount)" else "Notifications")
            }
            Spacer(Modifier.height(10.dp))
        }

        // ---------------- Stock movements ----------------
        if (hasAny("WAREHOUSE_MANAGER", "ADMIN")) {
            Button(onClick = onOpenGrapeMovement, modifier = Modifier.fillMaxWidth()) {
                Text("Grape Stock IN / OUT")
            }
            Spacer(Modifier.height(10.dp))

            Button(onClick = onOpenBottleMovement, modifier = Modifier.fillMaxWidth()) {
                Text("Bottle Stock IN / OUT")
            }
            Spacer(Modifier.height(10.dp))
        }

        // ---------------- Wine batches ----------------
        if (hasAny("ADMIN", "OPERATOR")) {
            Button(onClick = onOpenBatches, modifier = Modifier.fillMaxWidth()) {
                Text("Wine Batches")
            }
            Spacer(Modifier.height(10.dp))
        }

        Divider(Modifier.padding(vertical = 12.dp))

        // ---------------- Master data ----------------
        // Grape varieties
        if (hasAny("ADMIN", "WAREHOUSE_MANAGER")) {
            Button(onClick = onOpenGrapeVarieties, modifier = Modifier.fillMaxWidth()) {
                Text("Grape Varieties")
            }
            Spacer(Modifier.height(10.dp))
        }

        // Wine recipes
        if (hasAny("ADMIN")) {
            Button(onClick = onOpenWineRecipes, modifier = Modifier.fillMaxWidth()) {
                Text("Wine Recipes")
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}
