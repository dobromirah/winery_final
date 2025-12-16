package bg.tu.varna.si.winery.ui.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bg.tu.varna.si.winery.dto.NotificationDto

@Composable
fun NotificationsScreen(
    vm: NotificationsViewModel,
    onLogout: () -> Unit
) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { vm.loadUnread() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header row: title + logout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Notifications (Unread)", style = MaterialTheme.typography.titleLarge)
            Button(onClick = onLogout) {
                Text("Logout")
            }
        }

        Spacer(Modifier.height(12.dp))

        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }

        if (error != null) {
            Text("Error: $error", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
        }

        if (items.isEmpty() && !loading) {
            Text("No unread notifications ✅")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { n ->
                NotificationCard(
                    n = n,
                    onMarkRead = { vm.markAsRead(n.id) }
                )
            }
        }
    }
}

@Composable
private fun NotificationCard(
    n: NotificationDto,
    onMarkRead: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(n.type, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Text(n.message, style = MaterialTheme.typography.bodyMedium)

            if (n.createdAt != null) {
                Spacer(Modifier.height(6.dp))
                Text("Created: ${n.createdAt}", style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Mark as read",
                modifier = Modifier.clickable { onMarkRead() },
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
