package com.swengineering.team1.traffic_accident.nav

import com.swengineering.team1.traffic_accident.R

/**
 * MainNavigationBar.kt에서 사용하는 Screen 유형 결정
 */
enum class ScreenType(
    val route: String,
    val title: Int,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {
    Hotspot(
        "hotspot",
        R.string.label_hotspot,
        R.drawable.ic_hotspot_filled,
        R.drawable.ic_hotspot_outline
    ),

    Notification(
        "notification",
        R.string.label_notification,
        R.drawable.ic_notification_filled,
        R.drawable.ic_notification_outline
    ),

    Trends(
        "trends",
        R.string.label_trends,
        R.drawable.ic_chart_filled,
        R.drawable.ic_chart_outline
    ),

    Admin(
        "admin",
        R.string.label_admin_setting,
        R.drawable.ic_admin_filled,
        R.drawable.ic_admin_outline
    )
}