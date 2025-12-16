package bg.tu.varna.si.winery.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    showBack: Boolean,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },

                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },

                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Logout")
                    }
                }
            )
        }
    ) { padding ->
        content(padding)
    }
}
