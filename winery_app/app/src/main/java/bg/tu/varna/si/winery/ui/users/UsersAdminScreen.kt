package bg.tu.varna.si.winery.ui.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersAdminScreen(
    vm: UsersAdminViewModel,
    contentPadding: PaddingValues = PaddingValues()
) {
    val items by vm.items.collectAsState()
    val loading by vm.loading.collectAsState()
    val saving by vm.saving.collectAsState()
    val error by vm.error.collectAsState()

    var keycloakId by rememberSaveable { mutableStateOf("") }
    var fullName by rememberSaveable { mutableStateOf("") }

    val roles = listOf("OPERATOR", "WAREHOUSE_MANAGER")
    var selectedRole by rememberSaveable { mutableStateOf(roles.first()) }
    var roleExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.load() }

    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("User administration", style = MaterialTheme.typography.titleLarge)

        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (error != null) Text("Error: $error", color = MaterialTheme.colorScheme.error)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                OutlinedTextField(
                    value = keycloakId,
                    onValueChange = { keycloakId = it },
                    label = { Text("Keycloak user id (sub)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = roleExpanded,
                    onExpandedChange = { roleExpanded = !roleExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        value = selectedRole,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false }
                    ) {
                        roles.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r) },
                                onClick = {
                                    selectedRole = r
                                    roleExpanded = false
                                }
                            )
                        }
                    }
                }

                val canCreate = keycloakId.isNotBlank() && fullName.isNotBlank() && !saving

                Button(
                    enabled = canCreate,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        vm.create(
                            keycloakId = keycloakId.trim(),
                            fullName = fullName.trim(),
                            role = selectedRole
                        ) {
                            keycloakId = ""
                            fullName = ""
                        }
                    }
                ) {
                    Text(if (saving) "Saving..." else "Create user")
                }
            }
        }

        Text("All users", style = MaterialTheme.typography.titleMedium)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { u ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(u.fullName, style = MaterialTheme.typography.titleSmall)
                        Text("Role: ${u.role}")
                        Text("KeycloakId: ${u.keycloakId}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
