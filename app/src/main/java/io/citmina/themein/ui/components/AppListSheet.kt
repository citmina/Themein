package io.citmina.themein.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.core.graphics.drawable.toBitmap
import io.citmina.themein.model.AppInfo
import io.citmina.themein.utils.getInstalledApps

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListSheet(
    onDismiss: () -> Unit,
    onAppSelected: (AppInfo) -> Unit = {},
    selectedApps: List<AppInfo> = emptyList()
) {
    val context = LocalContext.current
    val apps = remember { context.getInstalledApps() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        windowInsets = WindowInsets(0),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("选择应用") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭"
                        )
                    }
                }
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(apps) { app ->
                    val isSelected = selectedApps.any { it.packageName == app.packageName }
                    AppItem(
                        app = app,
                        onClick = {
                            onAppSelected(app)
                            onDismiss()
                        },
                        isSelected = isSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun AppItem(
    app: AppInfo,
    onClick: () -> Unit,
    isSelected: Boolean
) {
    val alpha = if (isSelected) 0.5f else 1f
    val grayScaleMatrix = remember {
        ColorMatrix().apply {
            setToSaturation(if (isSelected) 0f else 1f)
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = !isSelected,
                onClick = onClick
            )
            .padding(8.dp)
            .alpha(alpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val bitmap = remember(app.icon) {
            (app.icon as? BitmapDrawable)?.bitmap 
                ?: app.icon.toBitmap()
        }.asImageBitmap()
        
        Image(
            bitmap = bitmap,
            contentDescription = app.appName,
            modifier = Modifier.size(48.dp),
            colorFilter = ColorFilter.colorMatrix(grayScaleMatrix)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = app.appName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = app.packageName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "已添加",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
} 