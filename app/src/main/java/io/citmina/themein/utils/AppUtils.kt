package io.citmina.themein.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import io.citmina.themein.model.AppInfo

fun Context.getInstalledApps(): List<AppInfo> {
    val packageManager = packageManager
    val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    return installedApps
        .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
        .map { appInfo ->
            AppInfo(
                packageName = appInfo.packageName,
                appName = packageManager.getApplicationLabel(appInfo).toString(),
                icon = packageManager.getApplicationIcon(appInfo),
                path = appInfo.sourceDir
            )
        }
        .sortedBy { it.appName }
} 