package com.swengineering.team1.traffic_accident.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.InsertChartOutlined
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import com.swengineering.team1.traffic_accident.R

/**
 * MainNavigationBar.kt에서 사용하는 Screen 유형 결정
 */
enum class ScreenType(
    val route: String,
    val title: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Hotspot(
        "hotspot",
        R.string.label_hotspot,
        Icons.Filled.LocationOn,
        Icons.Outlined.LocationOn
    ),

    Notification(
        "notification",
        R.string.label_notification,
        Icons.Filled.Notifications,
        Icons.Outlined.Notifications
    ),

    Trends(
        "trends",
        R.string.label_trends,
        Icons.Filled.InsertChart,
        Icons.Outlined.InsertChartOutlined
    ),

    Admin(
        "admin",
        R.string.label_admin_setting,
        Icons.Filled.AdminPanelSettings,
        Icons.Outlined.AdminPanelSettings
    )
}