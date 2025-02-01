package io.citmina.themein.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.citmina.themein.model.AppInfo

data class ResourceItem(
    val key: String,
    val value: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppResourceScreen(
    app: AppInfo,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(app.appName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { padding ->
        // 模拟数据
        val resources = List(10) { index ->
            ResourceItem(
                key = "资源 ${index + 1}",
                value = "${index + 1}${index + 1}${index + 1}${index + 1}"
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(resources) { resource ->
                ResourceRow(resource)
            }
        }
    }
}

@Composable
private fun ResourceRow(
    resource: ResourceItem
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 左侧 key
            Text(
                text = resource.key,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            
            // 分隔线
            Spacer(modifier = Modifier.width(16.dp))
            
            // 右侧 value
            Text(
                text = resource.value,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
} 