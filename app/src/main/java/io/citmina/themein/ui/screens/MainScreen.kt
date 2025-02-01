package io.citmina.themein.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import io.citmina.themein.navigation.NavigationItem
import io.citmina.themein.ui.components.AppListSheet
import io.citmina.themein.ui.components.BottomNavigationBar
import io.citmina.themein.model.AppInfo
import io.citmina.themein.data.AppPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesManager = remember { AppPreferencesManager(context) }

    var selectedItem by remember { mutableStateOf(NavigationItem.Home) }
    var showAppList by remember { mutableStateOf(false) }
    var currentApp by remember { mutableStateOf<AppInfo?>(null) }
    
    val selectedApps = remember { mutableStateListOf<AppInfo>() }
    
    // 在组件首次加载时初始化应用列表
    LaunchedEffect(Unit) {
        val savedApps = withContext(Dispatchers.IO) {
            preferencesManager.getSelectedApps()
        }
        
        // 转换保存的应用信息为完整的 AppInfo 对象
        val apps = savedApps.mapNotNull { savedApp ->
            try {
                val packageManager = context.packageManager
                val applicationInfo = packageManager.getApplicationInfo(savedApp.packageName, 0)
                AppInfo(
                    packageName = savedApp.packageName,
                    appName = savedApp.appName,
                    icon = packageManager.getApplicationIcon(applicationInfo),
                    path = savedApp.path
                )
            } catch (e: Exception) {
                null
            }
        }
        
        selectedApps.clear()
        selectedApps.addAll(apps)
    }
    
    val screenWidth = LocalConfiguration.current.screenWidthDp
    
    BackHandler(enabled = currentApp != null) {
        currentApp = null
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { 
            AnimatedVisibility(
                visible = currentApp == null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                BottomNavigationBar(
                    selectedItem = selectedItem,
                    onItemSelected = { selectedItem = it }
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentApp,
            transitionSpec = {
                if (targetState != null) {
                    // 进入资源页面：从右向左滑入
                    (slideInHorizontally(
                        initialOffsetX = { screenWidth },
                        animationSpec = tween(300)
                    ) + fadeIn(
                        animationSpec = tween(150)
                    )) with (slideOutHorizontally(
                        targetOffsetX = { -screenWidth },
                        animationSpec = tween(300)
                    ) + fadeOut(
                        animationSpec = tween(150)
                    ))
                } else {
                    // 返回主页面：从左向右滑入
                    (slideInHorizontally(
                        initialOffsetX = { -screenWidth },
                        animationSpec = tween(300)
                    ) + fadeIn(
                        animationSpec = tween(150)
                    )) with (slideOutHorizontally(
                        targetOffsetX = { screenWidth },
                        animationSpec = tween(300)
                    ) + fadeOut(
                        animationSpec = tween(150)
                    ))
                }
            }
        ) { targetApp ->
            if (targetApp != null) {
                AppResourceScreen(
                    app = targetApp,
                    onBackClick = { currentApp = null }
                )
            } else {
                AnimatedContent(
                    targetState = selectedItem,
                    transitionSpec = {
                        val (initialOffset, targetOffset) = if (targetState.ordinal > initialState.ordinal) {
                            screenWidth to -screenWidth  // 向左滑动
                        } else {
                            -screenWidth to screenWidth  // 向右滑动
                        }
                        
                        (slideInHorizontally(
                            initialOffsetX = { initialOffset },
                            animationSpec = tween(300)
                        ) + fadeIn(
                            animationSpec = tween(150)
                        )) with (slideOutHorizontally(
                            targetOffsetX = { targetOffset },
                            animationSpec = tween(300)
                        ) + fadeOut(
                            animationSpec = tween(150)
                        ))
                    }
                ) { targetItem ->
                    when (targetItem) {
                        NavigationItem.Home -> HomeScreen(
                            modifier = Modifier.padding(innerPadding),
                            selectedApps = selectedApps,
                            onAddClick = { showAppList = true },
                            onRemoveApp = { app ->
                                selectedApps.remove(app)  // 直接从列表中移除
                                scope.launch {
                                    preferencesManager.updateSelectedApps(selectedApps.toList())
                                }
                            },
                            onAppClick = { app ->
                                currentApp = app
                            }
                        )
                        NavigationItem.Settings -> SettingsScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
        
        if (showAppList) {
            AppListSheet(
                onDismiss = { showAppList = false },
                onAppSelected = { app ->
                    if (!selectedApps.contains(app)) {
                        selectedApps.add(app)
                        scope.launch {
                            preferencesManager.updateSelectedApps(selectedApps.toList())
                        }
                    }
                },
                selectedApps = selectedApps
            )
        }
    }
} 